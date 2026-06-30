package it.unipi.largescale.DiscoverEurope.DTO.mongodb;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Field;

/**
 * DTO representing an individual negative review.
 * Designed to be embedded within aggregate review reports (NegativeReviewsReportDTO).
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class EmbeddedForNegativeReviewDTO {
    @Field("user")
    private String userId;
    private double rating;
    private String comment;
}
