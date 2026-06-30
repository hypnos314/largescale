package it.unipi.largescale.DiscoverEurope.controller.mongodb;

import it.unipi.largescale.DiscoverEurope.model.mongodb.TravelPackage;
import it.unipi.largescale.DiscoverEurope.service.mongodb.TravelPackage_Service_Mongo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Travel Package's REST Controller.
 */
@RestController
@RequestMapping("/api/packages")
public class TravelPackage_Controller_Mongo {
    @Autowired
    private TravelPackage_Service_Mongo travelPackageService;

    /**
     * Retrieves the full details of a specific travel package.
     * @param packageId the unique identifier of the travel package.
     * @return a {@link ResponseEntity} containing the {@link TravelPackage} details.
     */
    @GetMapping("/{packageId}")
    public ResponseEntity<?> getPackageDetails(@PathVariable String packageId) {
        try {
            TravelPackage travelPackage = travelPackageService.getPackageDetails(packageId);
            return ResponseEntity.ok(travelPackage);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    /**
     * Retrieves a list of travel packages suggested for the user based on their last questionnaire results.
     * @param userId the unique identifier of the user.
     * @return a {@link ResponseEntity} containing a list of recommended {@link TravelPackage} objects.
     */
    @GetMapping("/suggested/{userId}")
    public ResponseEntity<List<TravelPackage>> getSuggestedPackages(@PathVariable String userId) {
        try {
            List<TravelPackage> packages = travelPackageService.getSuggestedPackages(userId);
            return ResponseEntity.ok(packages);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
