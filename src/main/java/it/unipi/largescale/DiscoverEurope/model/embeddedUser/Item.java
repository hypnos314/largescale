package it.unipi.largescale.DiscoverEurope.model.embeddedUser;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.Instant;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Item {
    @Field("package_id")
    private String packageId;
    private String name;
    private double price;
    @Field("added_at")
    private Instant addedAt;
}
