package it.unipi.largescale.DiscoverEurope.model.mongodb.embeddedPackage;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Embedded document representing a physical address.
 * Nested within hotel details to provide structured location data.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Address {
    private String city;
    private String country;
    private String street;
    private String zip;
}
