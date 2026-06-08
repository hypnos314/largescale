package it.unipi.largescale.DiscoverEurope.DTO;

import it.unipi.largescale.DiscoverEurope.model.embeddedUser.Credentials;
import lombok.Data;

@Data
public class LoginDTO {
    private String email;
    private String password;
}
