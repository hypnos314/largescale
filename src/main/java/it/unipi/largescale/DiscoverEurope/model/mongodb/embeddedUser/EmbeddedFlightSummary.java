package it.unipi.largescale.DiscoverEurope.model.mongodb.embeddedUser;

import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Field;

/**
 * Embedded document summarizing the complete flight itinerary (outbound and return).
 * Used within Items and Orders to link specific travel packages to actual flight selections.
 */
@Data
public class EmbeddedFlightSummary {
    @Field("outbound")
    private EmbeddedFlight outboundFlight;
    @Field("return")
    private EmbeddedFlight returnFlight;
}