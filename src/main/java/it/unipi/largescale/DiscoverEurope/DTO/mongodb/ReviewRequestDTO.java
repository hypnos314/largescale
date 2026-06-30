package it.unipi.largescale.DiscoverEurope.DTO.mongodb;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for submitting a travel package review.
 * Contains the reviewer's details, the target package information, the numerical rating,
 * textual feedback, and a flag indicating if the trip was a surprise package.
 */
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
