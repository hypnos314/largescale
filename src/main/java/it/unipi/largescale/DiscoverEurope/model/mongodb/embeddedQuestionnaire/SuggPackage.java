package it.unipi.largescale.DiscoverEurope.model.mongodb.embeddedQuestionnaire;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.mapping.Field;
import tools.jackson.databind.annotation.JsonSerialize;
import tools.jackson.databind.ser.std.ToStringSerializer;

import org.springframework.data.annotation.Transient;

import java.time.Instant;

/**
 * Embedded document representing an individual travel package suggested to the user.
 * Employs the @Transient annotation for dynamic fields (like prices and specific flights)
 * that are computed at runtime based on real-time availability, rather than stored persistently in the DB.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class SuggPackage {
    @Field("package_id")
    @JsonSerialize(using = ToStringSerializer.class)
    private ObjectId packageId;
    @Field("match_score")
    private double matchScore;
    private String name;

    @Transient
    private double price;
    @Transient
    private double basePrice;
    @Transient
    private int durationDays;
    @Transient
    @JsonSerialize(using = ToStringSerializer.class)
    private ObjectId outboundFlightId;
    @Transient
    @JsonSerialize(using = ToStringSerializer.class)
    private ObjectId returnFlightId;
    @Transient
    private Instant departureDate;
    @Transient
    private Instant returnDate;
}
