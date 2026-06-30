package it.unipi.largescale.DiscoverEurope.model.neo4j;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Node;
import org.springframework.data.neo4j.core.schema.Relationship;
import org.springframework.data.neo4j.types.GeographicPoint2d;

/**
 * Neo4j Node entity representing a specific Point of Interest.
 * Defines relationships such as BELONGS_TO and LOCATED_IN.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Node("POI")
public class POINode {
    @Id
    private String id;
    private String name;
    private String category;
    private String city;
    private GeographicPoint2d location;

    @Relationship(type = "BELONGS_TO", direction = Relationship.Direction.OUTGOING)
    private CategoryNode categoryNode;
    @Relationship(type = "LOCATED_IN", direction = Relationship.Direction.OUTGOING)
    private CityNode cityNode;
}