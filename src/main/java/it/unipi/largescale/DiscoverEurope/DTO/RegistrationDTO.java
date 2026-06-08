package it.unipi.largescale.DiscoverEurope.DTO;

import it.unipi.largescale.DiscoverEurope.model.embeddedUser.Credentials;
import it.unipi.largescale.DiscoverEurope.model.embeddedUser.IdentityDocument;
import it.unipi.largescale.DiscoverEurope.model.embeddedUser.PersonalInfo;
import lombok.Data;

import java.util.List;

@Data
public class RegistrationDTO {
    private Credentials credentials;
    private PersonalInfo personalInfo;
    private List<IdentityDocument> identityDocuments;
}
