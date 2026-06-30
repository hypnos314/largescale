package it.unipi.largescale.DiscoverEurope.model.mongodb.embeddedUser;

import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.mapping.Field;
import org.bson.types.ObjectId;
import lombok.Data;
import tools.jackson.databind.annotation.JsonSerialize;
import tools.jackson.databind.ser.std.ToStringSerializer;
import java.time.Instant;
import java.util.List;

/**
 * Embedded document representing an individual travel package currently in the user's cart.
 */
@Data
public class Item {
    @Field("package_id")
    @JsonSerialize(using = ToStringSerializer.class)
    private ObjectId packageId;
    private String name;
    private String destination;
    @Field("added_at")
    private Instant addedAt;
    @Field("is_surprise")
    private boolean isSurprise;
    private double price;
    @Field("flight_summary")
    private EmbeddedFlightSummary flightSummary;
    @Transient
    private String censoredTitle;
    private List<String> clues;
}