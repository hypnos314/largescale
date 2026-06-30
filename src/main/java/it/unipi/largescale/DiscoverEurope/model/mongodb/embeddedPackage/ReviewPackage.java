package it.unipi.largescale.DiscoverEurope.model.mongodb.embeddedPackage;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.Instant;

/**
 * Embedded document containing a user review.
 * Store a subset of the latest reviews directly inside the travel package.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ReviewPackage {
    @Field("user_name")
    private String userName;
    @Field("package_title")
    private String packageTitle;
    private double rating;
    private String comment;
    @Field("created_at")
    private Instant createdAt;
}
