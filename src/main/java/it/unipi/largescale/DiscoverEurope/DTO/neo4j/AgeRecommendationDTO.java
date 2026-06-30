package it.unipi.largescale.DiscoverEurope.DTO.neo4j;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for age-based travel package recommendations.
 * Used in the "For You" section to suggest unpurchased packages that are highly
 * popular among users in the same demographic age range.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AgeRecommendationDTO {
    private String packageId;
    private String suggestedPackage;
    private Double price;
    private Double avgRating;
    private Integer numberOfReviews;
    private int countSimilarAgeTarget;
    private String analyzedAgeTarget;
}
