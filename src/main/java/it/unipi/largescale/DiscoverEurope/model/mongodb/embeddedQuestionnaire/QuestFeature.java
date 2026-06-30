package it.unipi.largescale.DiscoverEurope.model.mongodb.embeddedQuestionnaire;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.List;

/**
 * Embedded document capturing the exact travel preferences submitted by the user.
 * Acts as the input parameter set for the recommendation matching algorithm.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class QuestFeature {
    @Field("preferred_season")
    private String preferredSeason;
    @Field("travel_style")
    private String travelStyle;
    private List<String> interests;
    @Field("departure_city")
    private String departureCity;
    @Field("budget_limit")
    private double budgetLimit;
    @Field("is_surprise")
    private boolean isSurprise;
    @Field("age_range")
    private String ageRange;
}
