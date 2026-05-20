package it.unipi.largescale.DiscoverEurope.model.embeddedUser;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.Instant;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Order {
    @Field("package_id")
    private String packageId;

    private String destination;
    private Instant date;
    private double price;
    @Field("order_status")
    private OrderStatus orderStatus;
    @Field("is_surprise")
    private boolean isSurprise;
    private String name;
}
