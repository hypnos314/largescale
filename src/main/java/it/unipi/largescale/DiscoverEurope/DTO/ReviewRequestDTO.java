package it.unipi.largescale.DiscoverEurope.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReviewRequestDTO {
    private String userId;
    private String userName;
    private String packageId;
    private String packageTitle;
    private double rating;
    private String comment;
    private boolean isSurprise;
}
