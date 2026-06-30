package it.unipi.largescale.DiscoverEurope.DTO.mongodb;

import it.unipi.largescale.DiscoverEurope.model.mongodb.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;

/**
 * DTO representing a user profile.
 * Flattens the complex User entity architecture to safely transmit personal data
 * to the client interface without exposing sensitive internal fields like passwords.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserDTO {
    private String name;
    private String lastName;
    private String email;
    private LocalDate birthdate;
    private String phone;
    private String nationality;

    /**
     * Factory method to map a database User entity into a UserDTO.
     */
    public static UserDTO fromEntity(User user) {
        return new UserDTO(
                user.getPersonalInfo().getFirstName(),
                user.getPersonalInfo().getLastName(),
                user.getCredentials() != null ? user.getCredentials().getEmail() : null,
                user.getPersonalInfo().getDateOfBirth(),
                user.getPersonalInfo().getPhone(),
                user.getPersonalInfo().getNationality()
        );
    }
}
