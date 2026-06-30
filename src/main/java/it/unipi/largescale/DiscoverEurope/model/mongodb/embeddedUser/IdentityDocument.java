package it.unipi.largescale.DiscoverEurope.model.mongodb.embeddedUser;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Field;
import java.time.LocalDate;

/**
 * Embedded document containing identity verification details.
 * Kept strictly within the User document to ensure compliance and privacy.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class IdentityDocument {
    private String type;
    private String number;
    @Field("expiry_date")
    private LocalDate expiryDate;
}
