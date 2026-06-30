package it.unipi.largescale.DiscoverEurope.model.mongodb.embeddedPackage;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.geo.GeoJsonPoint;

/**
 * Embedded document representing a localized activity or attraction within a package.
 * The @JsonIgnoreProperties annotation ensures clean JSON serialization
 * by hiding redundant internal coordinate fields.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PointOfInterest {
    private String name;
    private String category;
    @JsonIgnoreProperties({"x", "y"})
    private GeoJsonPoint location;
}
