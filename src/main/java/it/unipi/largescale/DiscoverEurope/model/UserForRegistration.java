package it.unipi.largescale.DiscoverEurope.model;

import it.unipi.largescale.DiscoverEurope.model.embeddedUser.*;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "users")
public class UserForRegistration {
    @Id
    private String id;
    private Credentials credentials;
    @Field("personal_info")
    private PersonalInfo personalInfo;
    @Field("identity_documents")
    private List<IdentityDocument> identityDocuments;
    private Cart cart;
    private List<Order> orders;
}
