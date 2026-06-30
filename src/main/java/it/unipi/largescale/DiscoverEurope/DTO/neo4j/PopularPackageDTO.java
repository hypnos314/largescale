package it.unipi.largescale.DiscoverEurope.DTO.neo4j;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for highlighting trending or highly-rated travel packages.
 * Used in the "Popular" section to display packages with the highest average ratings
 * and significant review volumes over recent periods.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PopularPackageDTO {
    private String packageId;
    private String packageName;
    private Double avgRating;
    private Double price;
    private Integer totalReviews;
}
