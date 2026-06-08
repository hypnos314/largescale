package it.unipi.largescale.DiscoverEurope.DTO;

import it.unipi.largescale.DiscoverEurope.model.embeddedPackage.Feature;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class QuestionnaireRequestDTO {
        private String userId;
        private Feature preferences;
}

