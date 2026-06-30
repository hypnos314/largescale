package it.unipi.largescale.DiscoverEurope.DTO.mongodb;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Field;
import java.util.List;

/**
 * DTO identifying shared hotels among highly-rated packages in a specific city.
 * Used in administrative analytics to find structural overlaps between top-performing travel offers.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SharedHotelDTO {
    @Field("_id")
    private String nameHotel;
    private int frequency;
    @Field("involved_packages")
    private List<PackageInfo> involvedPackages;

    /**
     * Nested DTO containing basic identification data for the involved packages.
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PackageInfo {
        @Field("packageId")
        private String packageId;
        @Field("packageName")
        private String packageName;
    }
}
