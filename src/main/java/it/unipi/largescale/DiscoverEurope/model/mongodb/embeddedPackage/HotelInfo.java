package it.unipi.largescale.DiscoverEurope.model.mongodb.embeddedPackage;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Embedded document containing the core metadata of a hotel.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class HotelInfo {
    private String name;
    private int stars;
    private String website;
}
