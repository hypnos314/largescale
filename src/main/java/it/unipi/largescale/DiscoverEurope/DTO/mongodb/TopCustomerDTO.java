package it.unipi.largescale.DiscoverEurope.DTO.mongodb;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Field;

/**
 * DTO representing a top-tier customer based on purchase history.
 * Used for marketing campaigns, such as distributing promotional discounts to users
 * with the highest number of confirmed orders.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TopCustomerDTO {

    @Id
    private String id;
    @Field("first_name")
    private String firstName;
    @Field("last_name")
    private String lastName;
    @Field("email")
    private String email;
    @Field("confirmed_orders")
    private Integer confirmedOrders;
}
