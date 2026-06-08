package it.unipi.largescale.DiscoverEurope.model.embeddedQuestionnaire;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.mapping.Field;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SuggPackage {
    @Field("package_id")
    private ObjectId packageId;
    @Field("match_score")
    private double matchScore;
    private String name;

    private double price;
    private int durationDays;
}
