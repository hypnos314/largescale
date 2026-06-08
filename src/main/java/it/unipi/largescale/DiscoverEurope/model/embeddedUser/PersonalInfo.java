package it.unipi.largescale.DiscoverEurope.model.embeddedUser;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Field;
import org.springframework.data.annotation.Transient;

import java.time.LocalDate;
import java.time.Period;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PersonalInfo {
    @Field("first_name")
    private String firstName;

    @Field("last_name")
    private String lastName;

    private String phone;
    private String nationality;
    @Field("date_of_birth")
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
