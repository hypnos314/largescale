package it.unipi.largescale.DiscoverEurope.model.mongodb;

import it.unipi.largescale.DiscoverEurope.model.mongodb.embeddedFlight.FlightDetail;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.Instant;

/**
 * MongoDB document entity representing a flight route.
 * Utilizes a compound index on departure city, arrival city, and departure time
 * to heavily optimize flight search queries.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "flights")
@CompoundIndexes({@CompoundIndex(name = "idx_flight_search", def = "{'departure.city': 1, 'arrival.city': 1, 'departure.at': 1}")})
public class Flight {
    @Id
    private ObjectId id;
    @Field("route_id")
    private String routeId;
    private String airline;
    private double price;
    @Field("duration_minutes")
    private int durationMinutes;
    private FlightDetail departure;
    private FlightDetail arrival;
}