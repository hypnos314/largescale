package it.unipi.largescale.DiscoverEurope.model.mongodb.embeddedQuestionnaire;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.mapping.Field;
import tools.jackson.databind.annotation.JsonSerialize;
import tools.jackson.databind.ser.std.ToStringSerializer;

import java.time.Instant;
import java.util.List;

/**
 * Embedded document representing the output of the recommendation engine.
 * Contains the list of suggested packages and records the user's final selection.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Suggestion {
    @Field("suggested_at")
    private Instant suggestedAt;
    private List<SuggPackage> packages;
    @Field("user_selection")
    @JsonSerialize(using = ToStringSerializer.class)
    private ObjectId userSelection;
}
