package it.unipi.largescale.DiscoverEurope.model.embeddedUser;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Field;

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
    private int age;
}
