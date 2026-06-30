package it.unipi.largescale.DiscoverEurope.service.mongodb;

import it.unipi.largescale.DiscoverEurope.DTO.mongodb.*;
import it.unipi.largescale.DiscoverEurope.repository.mongodb.Questionnaire_MongoInterface;
import it.unipi.largescale.DiscoverEurope.repository.mongodb.Review_MongoInterface;
import it.unipi.largescale.DiscoverEurope.repository.mongodb.TravelPackage_MongoInterface;
import it.unipi.largescale.DiscoverEurope.repository.mongodb.User_MongoInterface;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationOperation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.bson.Document;

import java.util.ArrayList;
import java.util.List;

/**
 * Service that handles administrative analytics and reporting operations on MongoDB.
 */
@Service
public class Admin_Analytics_Service_Mongo {

    @Autowired
    private MongoTemplate mongoTemplate;
    @Autowired
    private Questionnaire_MongoInterface questionnaireRepository;
    @Autowired
    private TravelPackage_MongoInterface travelPackageRepository;
    @Autowired
    private User_MongoInterface userRepository;
    @Autowired
    private Review_MongoInterface reviewRepository;

    /**
     * Retrieves a report of negative reviews. If a packageId is provided, filters by that package;
     * otherwise, returns the top 10 worst-performing packages based on negative keyword analysis.
     * @param packageId optional ID of the travel package
     * @return a list of {@link NegativeReviewsReportDTO} containing negative feedback metrics
     */
    public List<NegativeReviewsReportDTO> getNegativeReviewsReport(String packageId) {
        Criteria matchCriteria = new Criteria();
        if (packageId != null && !packageId.isEmpty()) {
            matchCriteria.and("package_id").is(new org.bson.types.ObjectId(packageId));
        }
        AggregationOperation matchStage = Aggregation.match(matchCriteria);

        AggregationOperation addFieldsStage = context -> Document.parse(
                "{ $addFields: { " +
                        "    is_negative: { " +
                        "      $and: [ " +
                        "        { $lte: ['$rating', 3] }, " +
                        "        { $regexMatch: { input: '$comment', regex: 'dirty|bad|delay|worst|terrible|disappointment|poor|dissatisfied|waste|average', options: 'i' } } " +
                        "      ] " +
                        "    } " +
                        "} }"
        );
        AggregationOperation groupStage = context -> Document.parse(
                "{ $group: { " +
                        "    _id: '$package_id', " +
                        "    packageName: { $first: '$package_title' }, " +
                        "    total_reviews: { $sum: 1 }, " +
                        "    total_negative_found: { $sum: { $cond: ['$is_negative', 1, 0] } }, " +
                        "    reviews: { " +
                        "      $push: { " +
                        "        $cond: [ " +
                        "          '$is_negative', " +
                        "          { user: '$user_id', rating: '$rating', comment: '$comment' }, " +
                        "          '$$REMOVE' " +
                        "        ] " +
                        "      } " +
                        "    } " +
                        "} }"
        );
        AggregationOperation projectStage = context -> Document.parse(
                "{ $project: { " +
                        "    _id: 1, " +
                        "    packageName: 1, " +
                        "    total_reviews: 1, " +
                        "    total_negative_found: 1, " +
                        "    reviews: 1, " +
                        "    negative_percentage: { " +
                        "      $round: [ " +
                        "        { $multiply: [ { $divide: ['$total_negative_found', '$total_reviews'] }, 100 ] }, " +
                        "        2 " +
                        "      ] " +
                        "    } " +
                        "} }"
        );

        List<AggregationOperation> operations = new ArrayList<>();
        operations.add(matchStage);
        operations.add(addFieldsStage);
        operations.add(groupStage);
        operations.add(projectStage);

        if (packageId != null && !packageId.isEmpty()) {
            operations.add(Aggregation.match(Criteria.where("total_negative_found").gt(0)));
        }
        else {
            operations.add(Aggregation.match(
                    Criteria.where("total_reviews").gt(4)
                            .and("total_negative_found").gt(4)
            ));
            operations.add(Aggregation.sort(Sort.Direction.DESC, "negative_percentage"));
            operations.add(Aggregation.limit(10));
        }

        Aggregation aggregation = Aggregation.newAggregation(operations);

        AggregationResults<NegativeReviewsReportDTO> results = mongoTemplate.aggregate(
                aggregation, "reviews", NegativeReviewsReportDTO.class
        );

        return results.getMappedResults();
    }

    /**
     * Retrieves statistics evaluating the success rate of surprise packages.
     * @return a {@link SurprisePackageStatsDTO} with performance metrics
     */
    public SurprisePackageStatsDTO getSurpriseStats() {
        return reviewRepository.getSurprisePackageStats();
    }

    /**
     * Retrieves a report calculating the average travel budget categorized by user age ranges.
     * @return a list of {@link BudgetByAgeRangeDTO}
     */
    public List<BudgetByAgeRangeDTO> getAvgBudgetByAgeReport() {
        return questionnaireRepository.getAverageBudgetByAgeRange();
    }

    /**
     * Identifies hotels shared among top-rated packages in a specified city.
     * @param city the destination city to analyze
     * @return a list of {@link SharedHotelDTO}
     */
    public List<SharedHotelDTO> getSharedHotels(String city) {
        List<SharedHotelDTO> results = travelPackageRepository.getSharedHotelTopPackages(city);
        return (results != null && !results.isEmpty()) ? results : new ArrayList<>();
    }

    /**
     * Calculates the total monthly revenue aggregated from confirmed orders.
     * @return a list of {@link MonthlyRevenueDTO} grouped by year and month
     */
    public List<MonthlyRevenueDTO> getMonthlyRevenue() {
        return userRepository.getMonthlyRevenue();
    }


    /**
     * Identifies top customers based on order volume and processes a simulated discount email campaign.
     * @return a list of {@link TopCustomerDTO} representing the rewarded users
     * @throws RuntimeException if no eligible customers are found
     */
    public List<TopCustomerDTO> sendDiscountToTopCustomers() {
        List<TopCustomerDTO> topCustomers = userRepository.getTopCustomers();
        if (topCustomers.isEmpty()) {
            throw new RuntimeException("Nessun cliente idoneo trovato per l'invio dei coupon.");
        }
        for (TopCustomerDTO customer : topCustomers) {
            String couponCode = "EUROPE10_" + customer.getFirstName().toUpperCase() + "_" + customer.getId().substring(0, 4);
            System.out.println("\n=======================================================");
            System.out.println("[EMAIL SERVICE] - Sending email...");
            System.out.println("To: " + customer.getEmail());
            System.out.println("Subject: A special gift for you from DiscoverEurope! ✈️");
            System.out.println("Body: Hi " + customer.getFirstName() + " " + customer.getLastName() + ",\n" +
                    "we want to thank you for being one of our most loyal customers with " +
                    customer.getConfirmedOrders() + " confirmed orders!\n" +
                    "For your next trip, use this 10% discount code: " + couponCode + "\n" +
                    "Have a great trip from the DiscoverEurope staff!");
            System.out.println("=======================================================");
        }
        return topCustomers;
    }
}
