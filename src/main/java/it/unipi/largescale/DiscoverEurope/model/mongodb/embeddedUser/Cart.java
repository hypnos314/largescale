package it.unipi.largescale.DiscoverEurope.model.mongodb.embeddedUser;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Field;
import java.util.ArrayList;
import java.util.List;

/**
 * Embedded document representing a user's shopping cart.
 * Aggregates pending items and calculates the running total before checkout.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Cart {
    private List<Item> items = new ArrayList<>();
    @Field("total_estimated")
    private double totalEstimated;
}
