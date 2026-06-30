package it.unipi.largescale.DiscoverEurope.repository.mongodb;

import it.unipi.largescale.DiscoverEurope.DTO.mongodb.BudgetByAgeRangeDTO;
import it.unipi.largescale.DiscoverEurope.model.mongodb.Questionnaire;
import org.springframework.data.mongodb.repository.Aggregation;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository for managing User Questionnaires in MongoDB.
 * Includes analytical aggregations for administrative reporting.
 */
@Repository
public interface Questionnaire_MongoInterface extends MongoRepository<Questionnaire, String> {
    Optional<Questionnaire> findTopByUserIdOrderBySubmittedAtDesc(String userId);
    List<Questionnaire> findByUserId(String userId);

    /**
     * Groups questionnaires by demographic age range
     * and calculates the average travel budget for each group.
     * @return list of aggregated budget reports
     */
    @Aggregation(pipeline = {
            "{ $group: { _id: '$preferences.age_range', raw_average: { $avg: '$preferences.budget_limit' }, total_questionnaires: { $sum: 1 } } }",
            "{ $project: { _id: 1, total_questionnaires: 1, average_budget: { $round: [ '$raw_average', 2 ] } } }",
            "{ $sort: { '_id': 1 } }"
    })
    List<BudgetByAgeRangeDTO> getAverageBudgetByAgeRange();
}


