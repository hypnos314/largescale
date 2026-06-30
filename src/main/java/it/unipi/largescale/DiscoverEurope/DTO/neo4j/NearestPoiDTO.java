package it.unipi.largescale.DiscoverEurope.DTO.neo4j;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for retrieving Points of Interest near a specific location.
 * Contains geospatial distance calculations and categorization, prioritized by
 * relevance or proximity to the booked hotel.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class NearestPoiDTO {
    private String poiId;
    private String poiName;
    private double distance;
    private String category;
    private Double longitude;
    private Double latitude;
}
