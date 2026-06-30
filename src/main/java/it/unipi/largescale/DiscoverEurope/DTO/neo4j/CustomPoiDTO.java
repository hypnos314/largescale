package it.unipi.largescale.DiscoverEurope.DTO.neo4j;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.List;

/**
 * DTO representing a custom Point of Interest (POI).
 * Encapsulates geospatial data using the standard GeoJSON format
 * while maintaining a transient reference to the original Neo4j node ID.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CustomPoiDTO {
    @Field("poi_id")
    @Transient
    private String poiId;
    private String name;
    private String category;
    private Location location;

    /**
     * Nested class representing the GeoJSON structure for geospatial indexing.
     */
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Location {
        private String type = "Point";
        private List<Double> coordinates;
    }
}

