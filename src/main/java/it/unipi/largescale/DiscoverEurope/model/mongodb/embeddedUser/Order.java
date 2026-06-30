package it.unipi.largescale.DiscoverEurope.model.mongodb.embeddedUser;

import it.unipi.largescale.DiscoverEurope.DTO.neo4j.CustomPoiDTO;
import org.springframework.data.mongodb.core.mapping.Field;
import org.bson.types.ObjectId;
import lombok.Data;
import tools.jackson.databind.annotation.JsonSerialize;
import tools.jackson.databind.ser.std.ToStringSerializer;
import java.time.Instant;
import java.util.List;

/**
 * Embedded document representing a finalized purchase order.
 * Serves as an immutable historical record of a transaction, capturing the exact price, flights,
 * and custom POIs in the user's history.
 */
@Data
public class Order {
    @Field("order_id")
    @JsonSerialize(using = ToStringSerializer.class)
    private ObjectId orderId;
    @Field("package_id")
    @JsonSerialize(using = ToStringSerializer.class)
    private ObjectId packageId;
    private String name;
    private String destination;
    private List<String> clues;
    @Field("purchase_date")
    private Instant purchaseDate;
    @Field("order_status")
    private String orderStatus;
    @Field("is_surprise")
    private boolean isSurprise;
    private double price;
    @Field("flight_summary")
    private EmbeddedFlightSummary flightSummary;
    @Field("custom_pois")
    private List<CustomPoiDTO> customPois;
    private String censoredTitle;
}