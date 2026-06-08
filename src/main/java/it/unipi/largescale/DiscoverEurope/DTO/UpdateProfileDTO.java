package it.unipi.largescale.DiscoverEurope.DTO;


import it.unipi.largescale.DiscoverEurope.model.embeddedUser.IdentityDocument;
import lombok.Data;

import java.util.List;

@Data
public class UpdateProfileDTO {
    private String newPassword;
    private List<IdentityDocument> identityDocuments;
}
