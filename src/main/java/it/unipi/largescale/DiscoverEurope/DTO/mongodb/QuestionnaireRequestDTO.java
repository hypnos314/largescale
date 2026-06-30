package it.unipi.largescale.DiscoverEurope.DTO.mongodb;

import it.unipi.largescale.DiscoverEurope.model.mongodb.embeddedQuestionnaire.QuestFeature;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for submitting a user's travel preference questionnaire.
 * Links a specific user ID to their selected travel features and interests.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class QuestionnaireRequestDTO {
        private String userId;
        private QuestFeature preferences;
}

