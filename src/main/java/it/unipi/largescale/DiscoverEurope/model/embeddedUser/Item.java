package it.unipi.largescale.DiscoverEurope.model.embeddedUser;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.Instant;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Item {
    @Field("package_id")
    private String packageId;
    private String name;
    //private String destination;
    private double price;
    @Field("is_surprise")
    private boolean isSurprise;
    @Field("censored_title")
    private String censoredTitle;
    @Field("return_date")
    private Instant returnDate;
    //@Field("departure_date")
    //private Instant departureDate;
    //private List<String> clues;
    @Field("added_at")
    private Instant addedAt;
}
