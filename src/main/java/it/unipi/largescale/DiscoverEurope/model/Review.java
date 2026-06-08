package it.unipi.largescale.DiscoverEurope.model;

//import jakarta.persistence.Id;
import org.springframework.data.annotation.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.Instant;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "reviews")
public class Review {
    @Id
    private String id;
    @Field("user_id")
    private String userId;
    @Field("package_id")
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
