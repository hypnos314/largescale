package it.unipi.largescale.DiscoverEurope.model.embeddedQuestionnaire;

import jakarta.persistence.Transient;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.Instant;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Suggestion {
    @Field("suggested_at")
    private Instant suggestedAt;
    private List<SuggPackage> packages;
    @Field("user_selection")
    private ObjectId userSelection;
}
