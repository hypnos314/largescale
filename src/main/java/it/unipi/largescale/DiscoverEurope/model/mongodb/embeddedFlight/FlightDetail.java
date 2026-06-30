package it.unipi.largescale.DiscoverEurope.model.mongodb.embeddedFlight;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Field;
import java.time.Instant;

/**
 * Embedded document representing a specific flight details.
 * Encapsulates airport information, city, and timing.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class FlightDetail {
    @Field("airport_iata")
    private String airportIata;
    private String city;
    private Instant at;
}
