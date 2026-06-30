package it.unipi.largescale.DiscoverEurope.DTO.mongodb;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.List;

/**
 * DTO for reporting negative review statistics of a specific travel package.
 * Includes total review counts, negative thresholds, and an embedded list of the critical reviews.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonPropertyOrder({
        "packageId",
        "packageName",
        "negativePercentage",
        "totalReviews",
        "totalNegativeFound",
        "reviews"
})
public class NegativeReviewsReportDTO {
    @Field("_id")
    private String packageId;
    private String packageName;
    @Field("total_reviews")
    private int totalReviews;
    @Field("total_negative_found")
    private int totalNegativeFound;
    @Field("negative_percentage")
    private double negativePercentage;
    private List<EmbeddedForNegativeReviewDTO> reviews;
}

