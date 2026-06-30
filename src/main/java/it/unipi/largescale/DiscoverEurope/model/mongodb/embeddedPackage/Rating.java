package it.unipi.largescale.DiscoverEurope.model.mongodb.embeddedPackage;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Field;

/**
 * Embedded document storing aggregated rating metrics.
 * Caches the average score and total review count directly inside the package document
 * to prevent expensive recalculations during high-volume read operations.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Rating {
    private Double average;
    @Field("total_reviews")
    private int totalReviews;
}
