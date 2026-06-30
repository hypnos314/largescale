package it.unipi.largescale.DiscoverEurope.model.mongodb.embeddedPackage;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.List;

/**
 * Embedded document defining the characteristics of a travel package.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PackageFeature {
    @Field("preferred_season")
    private String preferredSeason;
    @Field("travel_style")
    private String travelStyle;
    private List<String> interests;
}
