package it.unipi.largescale.DiscoverEurope.repository.mongodb;

import it.unipi.largescale.DiscoverEurope.DTO.mongodb.PackageScoreDTO;
import it.unipi.largescale.DiscoverEurope.model.mongodb.embeddedQuestionnaire.QuestFeature;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationOperation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Custom interface defining complex dynamic query for Travel Packages.
 */
@Repository
public class TravelPackage_Custom_MongoInterfaceImpl implements TravelPackage_Custom_MongoInterface {

    @Autowired
    private MongoTemplate mongoTemplate;

    /**
     * Executes the core scoring algorithm to find the top 3 best matching packages
     * based on user preferences, veto-rules, and reachable cities.
     */
    @Override
    public List<PackageScoreDTO> findTop3BestMatches(QuestFeature prefs, List<String> reachableCities) {
        List<String> userInterests = prefs.getInterests() != null ? prefs.getInterests() : List.of();
        String interestsArray = userInterests.stream().map(i -> "'" + i + "'").collect(Collectors.joining(","));
        String userCity = (prefs.getDepartureCity() != null) ? prefs.getDepartureCity() : "";
        String reachableArray = reachableCities.stream().map(c -> "'" + c + "'").collect(Collectors.joining(","));

        String matchStage = "{" +
                "$match: {" +
                "'city': { $ne: '" + userCity + "', $in: [" + reachableArray + "] }," +
                "'price': { $lte: " + prefs.getBudgetLimit() + " }" +
                "}" +
                "}";

        String addFieldsStage = "{" +
                "$addFields: {" +
                "score: {" +
                "$add: [" +
                    "{ $cond: [{ $eq: [{ $toLower: '$features.preferred_season' }, '" + prefs.getPreferredSeason().toLowerCase() + "'] }, 40, 0] }," +
                    "{ $cond: [{ $eq: [{ $toLower: '$features.travel_style' }, '" + prefs.getTravelStyle().toLowerCase() + "'] }, 30, 0] }," +
                    "{" +
                "$min: [" +
                "{ $multiply: [ { $size: { $setIntersection: [ { $ifNull: ['$features.interests', []] }, [" + interestsArray + "] ] } }, 10 ] }, 30 ] } ] } } }";

        AggregationOperation filterMatch = context -> context.getMappedObject(Document.parse(matchStage));
        AggregationOperation calculateScore = context -> context.getMappedObject(Document.parse(addFieldsStage));

        Aggregation aggregation = Aggregation.newAggregation(
                filterMatch,
                calculateScore,
                Aggregation.sort(
                        Sort.by(Sort.Direction.DESC, "score")
                                .and(Sort.by(Sort.Direction.DESC, "rating_summary.average"))
                                .and(Sort.by(Sort.Direction.ASC, "price"))
                ),
                Aggregation.limit(3),
                Aggregation.project("title", "score", "price").and("city").as("destinationCity")
        );
        AggregationResults<PackageScoreDTO> results = mongoTemplate.aggregate(
                aggregation, "travel_packages", PackageScoreDTO.class
        );
        return results.getMappedResults();
    }
}