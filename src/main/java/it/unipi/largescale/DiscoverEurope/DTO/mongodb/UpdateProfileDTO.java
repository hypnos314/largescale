package it.unipi.largescale.DiscoverEurope.DTO.mongodb;


import it.unipi.largescale.DiscoverEurope.model.mongodb.embeddedUser.IdentityDocument;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * DTO for updating user profile settings.
 * Carries sensitive modification requests, such as a new password or updated identity documents.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateProfileDTO {
    private String newPassword;
    private List<IdentityDocument> identityDocuments;
}
