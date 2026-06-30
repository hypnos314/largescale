package it.unipi.largescale.DiscoverEurope.model.mongodb.embeddedUser;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Field;
import org.springframework.data.annotation.Transient;
import java.time.LocalDate;
import java.time.Period;

/**
 * Embedded document storing basic demographic and contact information.
 * Calculates the user's current age dynamically via a @Transient field, ensuring the age
 * is always accurate relative to the current system date without needing background DB updates.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PersonalInfo {
    @Field("first_name")
    private String firstName;
    @Field("last_name")
    private String lastName;
    private String userName;
    private String phone;
    private String nationality;
    @Field("birth_date")
    private LocalDate dateOfBirth;
    @Transient
    private Integer age;
    
    public Integer getAge() {
        if (this.dateOfBirth != null) {
            return Period.between(this.dateOfBirth, LocalDate.now()).getYears();
        }
        return null;
    }
}
