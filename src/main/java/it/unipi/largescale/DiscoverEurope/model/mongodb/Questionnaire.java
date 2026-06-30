package it.unipi.largescale.DiscoverEurope.model.mongodb;

import it.unipi.largescale.DiscoverEurope.model.mongodb.embeddedQuestionnaire.QuestFeature;
import it.unipi.largescale.DiscoverEurope.model.mongodb.embeddedQuestionnaire.Suggestion;
import org.springframework.data.annotation.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import org.springframework.data.mongodb.core.mapping.FieldType;

import java.time.Instant;

/**
 * MongoDB document entity representing a user's submitted travel questionnaire.
 * Stores the user's travel preferences and the subsequent system-generated package suggestions.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "questionnaires")
public class Questionnaire {
    @Id
    private String id;
    @Field(name = "user_id", targetType = FieldType.OBJECT_ID)
    private String userId;
    @Field("submitted_at")
    private Instant submittedAt;
    private QuestFeature preferences;
    @Field("generated_suggestions")
    private Suggestion generatedSuggestions;
}
