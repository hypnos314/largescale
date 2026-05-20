package it.unipi.largescale.DiscoverEurope.model;

import it.unipi.largescale.DiscoverEurope.model.embeddedPackage.Feature;
import it.unipi.largescale.DiscoverEurope.model.embeddedQuestionnaire.Suggestion;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.Instant;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "questionnaires")
public class Questionnaire {
    @Id
    private String id;
    @Field("user_id")
    private String userId;
    @Field("submitted_at")
    private Instant submittedAt;
    private Feature preferences;
    @Field("generated_suggestions")
    private Suggestion generatedSuggestions;
}
