package it.unipi.largescale.DiscoverEurope.repository.neo4j;

import it.unipi.largescale.DiscoverEurope.DTO.neo4j.*;
import it.unipi.largescale.DiscoverEurope.model.neo4j.TravelPackageNode;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.neo4j.repository.query.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

/**
 * The Recommendation Repository for Neo4j.
 */
@Repository
public interface Recommendation_Neo4jInterface extends Neo4jRepository<TravelPackageNode, String> {

    /**
     * Retrieves popular packages trending over the last 6 months
     * based on high review volumes and average ratings.
     */
    @Query("MATCH (u:User)-[r:REVIEWED]->(p:TravelPackage) " +
            "WHERE r.timestamp >= datetime() - duration({months: 6}) " +
            "WITH p, round(avg(r.rating), 2) AS avgRating, count(r) AS totalReviews " +
            "WHERE totalReviews >= 10 " +
            "RETURN p.id AS packageId, p.title AS packageName, p.price AS price, avgRating, totalReviews " +
            "ORDER BY avgRating DESC, totalReviews DESC LIMIT 15")
    List<PopularPackageDTO> getPopularPackages();

    /**
     * Suggests packages sharing categories with the user's
     * most recently highly-rated travel experience.
     */
    @Query("MATCH (u:User {id: $userId})-[r:REVIEWED]->(last_reviewed:TravelPackage) " +
            "WITH u, last_reviewed, r ORDER BY r.timestamp DESC LIMIT 1 " +
            "MATCH (last_reviewed)-[:HAS_CATEGORY]->(c:Category) " +
            "MATCH (c)<-[:HAS_CATEGORY]-(rec:TravelPackage) " +
            "WHERE rec <> last_reviewed AND NOT (u)-[:PURCHASED]->(rec) AND rec.avg_rating >= 4 " +
            "WITH rec, last_reviewed, count(DISTINCT c) AS sharedCategory, collect(DISTINCT c.name) AS sharedCategoryNames " +
            "WITH rec, last_reviewed, sharedCategory, sharedCategoryNames, count { ()-[:REVIEWED]->(rec) } AS numberOfReviews " +
            "WITH rec, last_reviewed, sharedCategory, sharedCategoryNames, numberOfReviews, " +
            "     round((rec.avg_rating * numberOfReviews + 20.0) / (numberOfReviews + 5.0), 2) AS weightedScore " +
            "RETURN rec.id AS packageId, " +
            "       rec.title AS recommendedPackage, " +
            "       rec.avg_rating AS avgRating, " +
            "       numberOfReviews, " +
            "       sharedCategory, " +
            "       sharedCategoryNames, " +
            "       last_reviewed.title AS basedOnLastReview " +
            "ORDER BY weightedScore DESC,avgRating DESC, sharedCategory DESC LIMIT 10")
    List<RecommendedPackageDTO> getRecommendationsByInterests(String userId);

    /**
     * Suggests packages highly rated by other users
     * within the same age demographic as the target user.
     */
    @Query("MATCH (target:User {id: $userId}) " +
            "WITH target, duration.between(target.birth_date, date()).years AS target_age " +
            "WITH target, target_age, " +
            "  CASE " +
            "    WHEN target_age < 30 THEN 'Under 30' " +
            "    WHEN target_age >= 30 AND target_age <= 50 THEN 'between 30-50' " +
            "    ELSE 'Over 50' " +
            "  END AS target_age_group, " +
            "  CASE " +
            "    WHEN target_age < 30 THEN [date() - duration({years: 30}), date()] " +
            "    WHEN target_age >= 30 AND target_age <= 50 THEN [date() - duration({years: 50}), date() - duration({years: 30})] " +
            "    ELSE [date() - duration({years: 120}), date() - duration({years: 50})] " +
            "  END AS date_bounds " +
            "MATCH (others:User) " +
            "WHERE others.id <> target.id " +
            "  AND others.birth_date > date_bounds[0] " +
            "  AND others.birth_date <= date_bounds[1] " +
            "MATCH (others)-[:PURCHASED]->(p:TravelPackage) " +
            "WHERE NOT (target)-[:PURCHASED]->(p) AND p.avg_rating >= 4 " +
            "WITH p, target_age_group, count(others) AS countSimilarAgeTarget " +
            "WITH p, target_age_group, countSimilarAgeTarget, count { ()-[:REVIEWED]->(p) } AS numberOfReviews " +
            "WITH p, target_age_group, countSimilarAgeTarget, numberOfReviews, " +
            "     (p.avg_rating * numberOfReviews + 20.0) / (numberOfReviews + 5.0) AS weightedScore " +
            "RETURN p.id AS packageId, " +
            "       p.title AS suggestedPackage, " +
            "       p.price AS price, " +
            "       p.avg_rating AS avgRating, " +
            "       numberOfReviews, " +
            "       countSimilarAgeTarget, " +
            "       target_age_group AS analyzedAgeTarget " +
            "ORDER BY countSimilarAgeTarget DESC, weightedScore DESC LIMIT 10")
    List<AgeRecommendationDTO> getRecommendationsByAge(String userId);

    /**
     * Discovers Points of Interest within a 5km radius of the booked hotel,
     * excluding POIs already included in the package or manually added by the user.
     */
    @Query("MATCH (target:User {id: $userId})-[:PURCHASED]->(tp:TravelPackage {id: $packageId}) " +
            "MATCH (tp)-[:HAS_HOTEL]->(h:Hotel)-[:LOCATED_AT]->(city:CITY)<-[:LOCATED_IN]-(p:POI) " +
            // 1. SBLOCCO INDICE SPAZIALE: Uso point.distance direttamente nel WHERE
            "WHERE point.distance(p.location, h.location) <= 5000 " +
            "  AND NOT (tp)-[:INCLUDES]->(p) " +
            "  AND NOT p.id IN $alreadyAddedPoiIds " +
            // 2. Ora che abbiamo scremato, passiamo la distanza per il RETURN
            "WITH tp, p, h, point.distance(p.location, h.location) AS distance " +
            // Troviamo la categoria del POI
            "MATCH (p)-[:BELONGS_TO]->(c:Category) " +
            // Raggruppiamo nel caso un POI avesse più categorie (prendiamo la prima per sicurezza)
            "WITH p, distance, tp, collect(c.name)[0] AS categoryName " +
            "RETURN p.id AS poiId, p.name AS poiName, categoryName AS category, " +
            // Estraiamo le coordinate dal nodo spaziale di Neo4j
            "       p.location.longitude AS longitude, p.location.latitude AS latitude, " +
            "       round(distance) AS distance, $packageId AS packageId " +
            "ORDER BY distance ASC LIMIT 15")
    List<NearestPoiDTO> getNearestPOIs(
            @Param("userId") String userId,
            @Param("packageId") String packageId,
            @Param("alreadyAddedPoiIds") List<String> alreadyAddedPoiIds);
}