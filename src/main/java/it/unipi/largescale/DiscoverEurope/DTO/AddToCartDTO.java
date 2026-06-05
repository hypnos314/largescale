package it.unipi.largescale.DiscoverEurope.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AddToCartDTO {
    private String packageId;
    private boolean surprise;
}
