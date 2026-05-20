package it.unipi.largescale.DiscoverEurope.model.embeddedPackage;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Field;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FlightInfo {
    @Field("route_id")
    private String routeId;
    private String airline;
    private DepArr departure;
    private DepArr arrival;
}
