package it.unipi.largescale.DiscoverEurope.model.embeddedPackage;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class HotelInfo {
    private String name;
    private int stars;
    private String website;
}
