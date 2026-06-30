package it.unipi.largescale.DiscoverEurope.DTO.neo4j;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * DTO for interest-based personalized travel package recommendations.
 * Employs collaborative filtering to suggest unpurchased packages based on shared
 * categories and the user's past positive reviews.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RecommendedPackageDTO {
    private String packageId;
    private String recommendedPackage;
    private Double avgRating;
    private Integer numberOfReviews;
    private int sharedCategory;
    private List<String> sharedCategoryNames;
    private String basedOnLastReview;
}
