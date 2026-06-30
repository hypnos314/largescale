package it.unipi.largescale.DiscoverEurope.repository.neo4j;

import it.unipi.largescale.DiscoverEurope.DTO.neo4j.*;
import it.unipi.largescale.DiscoverEurope.model.neo4j.UserNode;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.neo4j.repository.query.Query;
import org.springframework.stereotype.Repository;
import java.util.List;

/**
 * Neo4j Repository dedicated to administrative graph analytics.
 */
@Repository
public interface Admin_Analytics_Neo4jInterface extends Neo4jRepository<UserNode, String> {

    /**
     * Identifies the most popular destinations sorted by user age groups.
     * Incorporates a weighted rating formula to prioritize highly reviewed packages.
     * @return list of top 3 destinations for each demographic bracket.
     */
    @Query("MATCH (u:User)-[:PURCHASED]->(pkg:TravelPackage)-[:DESTINATION]->(city:CITY) " +
            "WHERE u.birth_date IS NOT NULL AND pkg.avg_rating >= 4 " +
            "WITH city, pkg, duration.between(date(u.birth_date), date()).years AS age, count { ()-[:REVIEWED]->(pkg) } AS pkg_reviews " +
            "WITH city, pkg, pkg_reviews, CASE WHEN age < 30 THEN '1. Under 30' WHEN age >= 30 AND age <= 50 THEN '2. Between 30-50' ELSE '3. Over 50' END AS age_group " +
            "WITH age_group, city.name AS destination, count(pkg) AS total_purchases, avg(pkg.avg_rating) AS raw_rating, sum(pkg_reviews) AS total_reviews " +
            "WITH age_group, destination, total_purchases, " +
            "     (raw_rating * total_reviews + 20.0) / (total_reviews + 5.0) AS weighted_rating " +
            "ORDER BY age_group ASC, total_purchases DESC, weighted_rating DESC " +
            "WITH age_group, collect({city: destination, purchases: total_purchases, rating: round(weighted_rating, 1)})[0..3] AS top " +
            "RETURN age_group AS ageRange, " +
            "top[0].city + ' (' + toString(top[0].purchases) + ' purchases, score: ' + toString(top[0].rating) + ')' AS firstPlace, " +
            "coalesce(top[1].city + ' (' + toString(top[1].purchases) + ' purchases, score: ' + toString(top[1].rating) + ')', '-') AS secondPlace, " +
            "coalesce(top[2].city + ' (' + toString(top[2].purchases) + ' purchases, score: ' + toString(top[2].rating) + ')', '-') AS thirdPlace " +
            "ORDER BY ageRange ASC")
    List<PopularDestinationsDTO> getPopularDestinationsByAgeGroup();
}