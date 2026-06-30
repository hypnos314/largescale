package it.unipi.largescale.DiscoverEurope.model.neo4j;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Node;
import org.springframework.data.neo4j.core.schema.Relationship;
import org.springframework.data.neo4j.types.GeographicPoint2d;

/**
 * Neo4j Node entity representing a Hotel accommodation.
 * Utilizes an outgoing LOCATED_AT relationship to a CityNode.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Node("Hotel")
public class HotelNode {
    @Id
    private String id;
    private String name;
    private String city;
    private GeographicPoint2d location;

    @Relationship(type = "LOCATED_AT", direction = Relationship.Direction.OUTGOING)
    private CityNode cityNode;

}