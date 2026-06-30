package it.unipi.largescale.DiscoverEurope.DTO.mongodb;

import it.unipi.largescale.DiscoverEurope.model.mongodb.embeddedUser.Credentials;
import it.unipi.largescale.DiscoverEurope.model.mongodb.embeddedUser.IdentityDocument;
import it.unipi.largescale.DiscoverEurope.model.mongodb.embeddedUser.PersonalInfo;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * DTO for user registration.
 * Encapsulates all the necessary information required to create a new user account,
 * including login credentials, personal details, and identity verification documents.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RegistrationDTO {
    private Credentials credentials;
    private PersonalInfo personalInfo;
    private List<IdentityDocument> identityDocuments;
}
