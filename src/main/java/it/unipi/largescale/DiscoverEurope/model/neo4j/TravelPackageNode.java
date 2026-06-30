package it.unipi.largescale.DiscoverEurope.model.neo4j;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.neo4j.core.schema.*;

import java.util.List;

/**
 * Neo4j Node entity representing a Travel Package.
 * There are outgoing relationships to Categories, POIs, Destinations, and Hotels.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Node("TravelPackage")
public class TravelPackageNode {
    @Id
    private String id;
    private String title;
    private Double price;
    private Double avgRating;

    @Relationship(type = "HAS_CATEGORY", direction = Relationship.Direction.OUTGOING)
    private List<CategoryNode> categories;
    @Relationship(type = "INCLUDES", direction = Relationship.Direction.OUTGOING)
    private List<POINode> pois;
    @Relationship(type = "DESTINATION", direction = Relationship.Direction.OUTGOING)
    private CityNode destination;
    @Relationship(type = "HAS_HOTEL", direction = Relationship.Direction.OUTGOING)
    private HotelNode hotel;
}