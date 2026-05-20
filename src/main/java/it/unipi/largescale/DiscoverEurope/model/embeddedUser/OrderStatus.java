package it.unipi.largescale.DiscoverEurope.model.embeddedUser;

import lombok.Data;

@Data
public enum OrderStatus {
    CANCELLED,
    PENDING,
    CONFIRMED
}
