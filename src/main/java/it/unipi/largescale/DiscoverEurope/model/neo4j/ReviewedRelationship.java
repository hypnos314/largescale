package it.unipi.largescale.DiscoverEurope.model.neo4j;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.neo4j.core.schema.RelationshipId;
import org.springframework.data.neo4j.core.schema.RelationshipProperties;
import org.springframework.data.neo4j.core.schema.TargetNode;

import java.time.LocalDateTime;

/**
 * Neo4j Relationship entity representing the "REVIEWED" action.
 * Connects a UserNode to a TravelPackageNode, storing the assigned rating and the timestamp.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@RelationshipProperties
public class ReviewedRelationship {
    @RelationshipId
    private String internalId;

    @TargetNode
    private TravelPackageNode travelPackage;
    private Integer rating;
    private LocalDateTime timestamp;
}