package it.unipi.largescale.DiscoverEurope.DTO.mongodb;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for selecting a specific travel package.
 * Used in the questionnaire workflow to associate a user's generated profile
 * with their final package choice.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class SelectPackageDTO {
    private String packageId;
}
