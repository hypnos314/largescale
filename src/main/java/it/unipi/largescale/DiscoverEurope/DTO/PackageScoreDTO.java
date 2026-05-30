package it.unipi.largescale.DiscoverEurope.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PackageScoreDTO {
    private String id;
    private String title;
    private int score;
    private boolean perfectMatch;
}
