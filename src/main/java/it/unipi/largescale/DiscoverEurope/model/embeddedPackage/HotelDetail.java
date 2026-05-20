package it.unipi.largescale.DiscoverEurope.model.embeddedPackage;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.geo.GeoJsonPoint;
import org.springframework.data.mongodb.core.mapping.Field;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class HotelDetail {
    @Field("hotel_info")
    private HotelInfo hotelInfo;
    private GeoJsonPoint location;
    private Address address;
}
