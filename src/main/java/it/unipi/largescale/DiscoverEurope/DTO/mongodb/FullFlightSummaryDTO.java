package it.unipi.largescale.DiscoverEurope.DTO.mongodb;

import it.unipi.largescale.DiscoverEurope.model.mongodb.Flight;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO representing the complete flight itinerary for a confirmed order.
 * Encapsulates both outbound and return flight entities.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class FullFlightSummaryDTO {
    private Flight outboundFlight;
    private Flight returnFlight;
}
