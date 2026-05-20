package it.unipi.largescale.DiscoverEurope.model.embeddedPackage;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.geo.GeoJsonPoint;
import org.springframework.data.mongodb.core.index.GeoSpatialIndexType;
import org.springframework.data.mongodb.core.index.GeoSpatialIndexed;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PointOfInterest {
    private String name;
    private String category; //potrebbe essere enum
    @GeoSpatialIndexed(type = GeoSpatialIndexType.GEO_2DSPHERE) //crea un indice geospaziale, utile per performance
    private GeoJsonPoint location;
}
