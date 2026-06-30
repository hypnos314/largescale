package it.unipi.largescale.DiscoverEurope.model.neo4j;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.neo4j.core.schema.*;
import java.time.LocalDate;
import java.util.List;

/**
 * Neo4j Node entity representing a User in the graph.
 * Maintains stateful relationships with properties (@RelationshipProperties) pointing to the
 * packages they have PURCHASED and REVIEWED.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Node("User")
public class UserNode {
    @Id
    private String id;
    @Property("first_name")
    private String firstName;
    @Property("last_name")
    private String lastName;
    @Property("birth_date")
    private LocalDate birthDate;
    private String role;

    @Relationship(type = "REVIEWED", direction = Relationship.Direction.OUTGOING)
    private List<ReviewedRelationship> reviewedPackages;
    @Relationship(type = "PURCHASED", direction = Relationship.Direction.OUTGOING)
    private List<PurchasedRelationship> purchasedPackages;
}