package it.unipi.largescale.DiscoverEurope.DTO.neo4j;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for administrative reporting on popular travel destinations.
 * Aggregates and ranks the top three most visited or purchased destinations
 * categorized by specific user age demographics.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PopularDestinationsDTO {
    private String ageRange;
    private String firstPlace;
    private String secondPlace;
    private String thirdPlace;
}
