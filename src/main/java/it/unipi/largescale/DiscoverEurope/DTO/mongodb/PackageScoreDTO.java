package it.unipi.largescale.DiscoverEurope.DTO.mongodb;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Field;

/**
 * DTO representing the compatibility score of a travel package for a given user.
 * Used by the recommendation engine to rank packages and highlight perfect matches.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PackageScoreDTO {
    private String id;
    private String title;
    private int score;
    @Field("isPerfectMatch")
    private boolean IsPerfectMatch;
    private String destinationCity;
    private double price;
}
