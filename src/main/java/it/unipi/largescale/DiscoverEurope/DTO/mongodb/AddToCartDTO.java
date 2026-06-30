package it.unipi.largescale.DiscoverEurope.DTO.mongodb;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for adding a travel package to a user's shopping cart.
 * Encapsulates the selected package, specific flight choices, and the surprise trip flag.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class AddToCartDTO {
    private String packageId;
    private boolean surprise;
    private String outboundFlightId;
    private String returnFlightId;
}
