package it.unipi.largescale.DiscoverEurope.model.embeddedPackage;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Feature {
    // Attributi di TravelPackage con relativo costruttore
    @Field("preferred_season")
    private String preferredSeason; //potrebbe essere enum
    @Field("travel_style")
    private String travelStyle; //potrebbe essere enum
    private List<String> interests; //oppure lista di Interest dove Interest enum

    public Feature(String preferredSeason, String travelStyle, List<String> interests){
        this.preferredSeason = preferredSeason;
        this.travelStyle = travelStyle;
        this.interests = interests;
    }


    // Restanti attributi solo per Questionnaire (preferences)
    @Field("departure_city")
    private String departureCity;
    @Field("budget_limit")
    private double budgetLimit;
    @Field("is_surprise")
    private boolean isSurprise;
    @Field("age_range")
    private String ageRange;

}
