package it.unipi.largescale.DiscoverEurope.repository.mongodb;

import it.unipi.largescale.DiscoverEurope.DTO.mongodb.SurprisePackageStatsDTO;
import it.unipi.largescale.DiscoverEurope.model.mongodb.Review;
import org.springframework.data.mongodb.repository.Aggregation;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository for managing user Reviews in MongoDB.
 * Processes feedback metrics and calculates success rates for specific package types.
 */
@Repository
public interface Review_MongoInterface extends MongoRepository<Review, String> {
    List<Review> findTop3ByPackageIdOrderByCreatedAtDesc(String packageId);
    List<Review> findByPackageId(String packageId);

    /**
     * Determines the success rate of the personalization algorithm
     * by evaluating positive feedback exclusively on "Surprise" packages.
     * @return statistical summary of surprise package reviews
     */
    @Aggregation(pipeline = {
            "{ $match: { 'is_surprise': true } }",
            "{ $group: { " +
                    "    _id: null, " +
                    "    total_surprises: { $sum: 1 }, " +
                    "    five_stars: { $sum: { $cond: [{ $eq: ['$rating', 5] }, 1, 0] } }, " +
                    "    four_stars: { $sum: { $cond: [{ $eq: ['$rating', 4] }, 1, 0] } }, " +
                    "    total_positive: { $sum: { $cond: [{ $gte: ['$rating', 4] }, 1, 0] } } " +
                    "} }",
            "{ $project: { " +
                    "    _id: 0, " +
                    "    total_reviews: '$total_surprises', " +
                    "    five_star_count: '$five_stars', " +
                    "    four_star_count: '$four_stars', " +
                    "    total_success_rate: { $round: [ { $multiply: [ { $divide: ['$total_positive', '$total_surprises'] }, 100 ] }, 2 ] }, " +
                    "    excellence_rate: { $round: [ { $multiply: [ { $divide: ['$five_stars', '$total_surprises'] }, 100 ] }, 2 ] } " +
                    "} }"
    })
    SurprisePackageStatsDTO getSurprisePackageStats();
}
