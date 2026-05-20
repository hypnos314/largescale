package it.unipi.largescale.DiscoverEurope.model;

import it.unipi.largescale.DiscoverEurope.model.embeddedPackage.*;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "travel_packages")
public class TravelPackage {
    @Id
    private String id;
    private String title;
    private String city;
    private List<String> clues;
    private Feature features; //usare costruttore con 3 argomenti
    private List<PointOfInterest> activities;
    private HotelDetail hotel;
    @Field("flight_details")
    private FlightDetail flightDetails;
    @Field("rating_summary")
    private Rating ratingSummary;

    private double price;
    @Field("latest_reviews")
    private List<ReviewPackage> latestReviews;

}
