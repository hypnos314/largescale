package it.unipi.largescale.DiscoverEurope.repository.neo4j;

import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.neo4j.repository.query.Query;
import org.springframework.stereotype.Repository;
import it.unipi.largescale.DiscoverEurope.model.neo4j.UserNode;

/**
 * Synchronization Repository for maintaining graph consistency.
 * Consumes events triggered by primary database (MongoDB) mutations to keep
 * the Neo4j recommendation graph up-to-date in near real-time.
 */
@Repository
public interface Sync_Neo4jInterface extends Neo4jRepository<UserNode, String> {

    /**
     * Merge user data into the graph, ensuring properties are current.
     */
    @Query("MERGE (u:User {id: $userId}) " +
            "SET u.first_name = $firstName, " +
            "    u.last_name = $lastName, " +
            "    u.birth_date = date($birthDate), " +
            "    u.role = $role")
    void syncUser(String userId, String firstName, String lastName, String birthDate, String role);

    /**
     * Completely removes a user node and all its connected relationships from the graph.
     */
    @Query("MATCH (u:User {id: $userId}) DETACH DELETE u")
    void deleteUser(String userId);

    /**
     * Creates a PURCHASED relationship between a User and a Travel Package.
     */
    @Query("MATCH (u:User {id: $userId}) " +
            "MATCH (p:TravelPackage {id: $packageId}) " +
            "MERGE (u)-[r:PURCHASED]->(p) " +
            "SET r.timestamp = datetime($timestamp)")
    void createPurchasedRelation(String userId, String packageId, String timestamp);

    /**
     * Creates a REVIEWED relationship between a User and a Travel Package.
     */
    @Query("MATCH (u:User {id: $userId}) " +
            "MATCH (p:TravelPackage {id: $packageId}) " +
            "MERGE (u)-[r:REVIEWED]->(p) " +
            "SET r.timestamp = datetime($timestamp), " +
            "    r.rating = $rating")
    void createReviewedRelation(String userId, String packageId, String timestamp, Double rating);
}