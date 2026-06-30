package it.unipi.largescale.DiscoverEurope.model.mongodb;

import it.unipi.largescale.DiscoverEurope.model.mongodb.embeddedPackage.*;
import org.springframework.data.annotation.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.List;

/**
 * Core MongoDB document entity representing a complete travel package offering.
 * Aggregates all package details including features, hotel details, POIs (activities),
 * and a denormalized summary of the latest reviews to speed up read operations.
 * Indexed by city for rapid destination-based filtering.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "travel_packages")
public class TravelPackage {
    @Id
    private String id;
    private String title;

    @Indexed(name = "idx_city")
    private String city;
    private List<String> clues;
    private PackageFeature features;
    private List<PointOfInterest> activities;
    private HotelDetail hotel;
    @Field("rating_summary")
    private Rating ratingSummary;
    private double price;
    @Field("latest_reviews")
    private List<ReviewPackage> latestReviews;
}
