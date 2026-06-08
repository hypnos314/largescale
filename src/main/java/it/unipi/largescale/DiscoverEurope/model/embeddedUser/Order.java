package it.unipi.largescale.DiscoverEurope.model.embeddedUser;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.Instant;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Order {
    @Field("order_id")
    private String orderId;
    @Field("package_id")
    private String packageId;
    private String destination;
    @Field("purchase_date")
    private Instant purchaseDate;
    private double price;
    @Field("order_status")
    private String orderStatus;
    @Field("is_surprise")
    private boolean isSurprise;
    private String name;
    @Field("censored_title")
    private String censoredTitle;
    @Field("return_date")
    private Instant returnDate;
    @Field("departure_date")
    private Instant departureDate;
    private List<String> clues;
}
