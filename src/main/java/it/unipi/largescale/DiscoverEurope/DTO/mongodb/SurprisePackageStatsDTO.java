package it.unipi.largescale.DiscoverEurope.DTO.mongodb;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Field;

/**
 * DTO representing performance metrics for surprise packages.
 * Evaluates the success rate of the personalization algorithm by analyzing the distribution
 * of positive reviews (4 and 5 stars) specifically for surprise bookings.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class SurprisePackageStatsDTO {

    @Field("total_reviews")
    private Integer totalReviews;
    @Field("five_star_count")
    private Integer fiveStarCount;
    @Field("four_star_count")
    private Integer fourStarCount;
    @Field("total_success_rate")
    private Double totalSuccessRate;
    @Field("excellence_rate")
    private Double excellenceRate;
}
