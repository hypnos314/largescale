package it.unipi.largescale.DiscoverEurope.model.embeddedPackage;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Field;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FlightDetail {
    @Field("outbound_flight")
    private FlightInfo outboundFlight;
    @Field("return_flight")
    private FlightInfo returnFlight;

}
