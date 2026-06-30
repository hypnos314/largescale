package it.unipi.largescale.DiscoverEurope.model.mongodb;

import org.springframework.data.annotation.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import org.springframework.data.mongodb.core.mapping.FieldType;

import java.time.Instant;

/**
 * MongoDB document entity representing a user review for a travel package.
 * Includes a specific index on {@code package_id} to optimize queries fetching
 * all reviews associated with a single travel package.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "reviews")
public class Review {
    @Id
    private String id;
    @Field(name="user_id", targetType = FieldType.OBJECT_ID)
    private String userId;
    @Field(name="package_id", targetType = FieldType.OBJECT_ID)

    @Indexed(name = "package_id_idx")
    private String packageId;
    @Field("user_name")
    private String userName;
    @Field("package_title")
    private String packageTitle;
    private double rating;
    private String comment;
    @Field("created_at")
    private Instant createdAt;
    @Field("is_surprise")
    private boolean isSurprise;
}
