package it.unipi.largescale.DiscoverEurope.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PackageScoreDTO {
    private String id;
    private String title;
    private int score;
    private boolean perfectMatch;
    private String destinationCity;

    private double price;
    private Instant departureDate;
    private Instant returnDate;
}
