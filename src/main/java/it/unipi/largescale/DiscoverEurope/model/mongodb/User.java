package it.unipi.largescale.DiscoverEurope.model.mongodb;

import it.unipi.largescale.DiscoverEurope.model.mongodb.embeddedUser.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import java.util.List;

/**
 * MongoDB document entity representing a registered user in the system.
 * Aggregates personal information, identity documents, the current shopping cart,
 * and the user's entire order history all in a single document.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "users")
public class User {
    @Id
    private String id;
    private String role;
    private Credentials credentials;
    
    @Field("personal_info")
    private PersonalInfo personalInfo;
    
    @Field("identity_documents")
    private List<IdentityDocument> identityDocuments;
    
    private Cart cart;
    private List<Order> orders;
}
