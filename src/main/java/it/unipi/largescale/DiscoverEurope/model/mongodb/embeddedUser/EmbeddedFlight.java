package it.unipi.largescale.DiscoverEurope.model.mongodb.embeddedUser;

import org.springframework.data.mongodb.core.mapping.Field;
import org.bson.types.ObjectId;
import lombok.Data;
import tools.jackson.databind.annotation.JsonSerialize;
import tools.jackson.databind.ser.std.ToStringSerializer;
import java.time.Instant;

/**
 * Embedded document representing a flight instance within a user's cart or order.
 */
@Data
public class EmbeddedFlight {
    @Field("flight_id")
    @JsonSerialize(using = ToStringSerializer.class)
    private ObjectId flightId;
    @Field("departure_city")
    private String departureCity;
    @Field("arrival_city")
    private String arrivalCity;
    @Field("departure_date")
    private Instant departureDate;
    @Field("arrival_date")
    private Instant arrivalDate;
}