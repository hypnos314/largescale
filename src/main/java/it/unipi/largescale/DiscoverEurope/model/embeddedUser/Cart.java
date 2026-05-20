package it.unipi.largescale.DiscoverEurope.model.embeddedUser;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Cart {
    private List<Item> items;
    @Field("total_estimated")
    private double totalEstimated;
}
