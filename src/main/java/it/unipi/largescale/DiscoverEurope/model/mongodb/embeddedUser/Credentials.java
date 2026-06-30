package it.unipi.largescale.DiscoverEurope.model.mongodb.embeddedUser;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Field;

/**
 * Embedded document storing the user's authentication credentials.
 * Secures the application by isolating the hashed password from general profile data.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Credentials {
    private String email;
    @Field("password_hash")
    private String password;
}
