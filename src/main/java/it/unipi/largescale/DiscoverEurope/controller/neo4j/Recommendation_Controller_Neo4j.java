package it.unipi.largescale.DiscoverEurope.controller.neo4j;

import it.unipi.largescale.DiscoverEurope.DTO.neo4j.*;
import it.unipi.largescale.DiscoverEurope.service.neo4j.Recommendation_Service_Neo4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/neo4j/recommendations")
public class Recommendation_Controller_Neo4j {

    @Autowired
    private Recommendation_Service_Neo4j recommendationService;

    /**
     * Retrieves the most popular travel packages based on total purchased in the last six months with
     * the highest rating.
     * @return a {@link ResponseEntity} with a list of {@link PopularPackageDTO}.
     */
    @GetMapping("/popular")
    public ResponseEntity<List<PopularPackageDTO>> getPopular() {
        return ResponseEntity.ok(recommendationService.getPopularPackages());
    }

    /**
     * Retrieves personalized package recommendations based on user interests.
     * @param userId the unique identifier of the user.
     * @return a {@link ResponseEntity} with a list of {@link RecommendedPackageDTO}.
     */
    @GetMapping("/for-you/interests/{userId}")
    public ResponseEntity<List<RecommendedPackageDTO>> getForYouByInterests(@PathVariable String userId) {
        return ResponseEntity.ok(recommendationService.getForYouByInterests(userId));
    }

    /**
     * Retrieves packages popular among users within the same age group.
     * @param userId the unique identifier of the user.
     * @return a {@link ResponseEntity} with a list of {@link AgeRecommendationDTO}.
     */
    @GetMapping("/for-you/age/{userId}")
    public ResponseEntity<List<AgeRecommendationDTO>> getForYouByAge(@PathVariable String userId) {
        return ResponseEntity.ok(recommendationService.getRecommendationsByAge(userId));
    }

    /**
     * Finds nearby Points of Interest relative to the hotel of a specific purchased package.
     * @param userId the user identifier.
     * @param packageId the booked package identifier.
     * @param alreadyAddedPoiIds list of POI IDs to exclude (already selected by the user).
     * @return a {@link ResponseEntity} with a list of {@link NearestPoiDTO}.
     */
    @GetMapping("/users/{userId}/packages/{packageId}/nearest-pois")
    public ResponseEntity<List<NearestPoiDTO>> getNearestPois(
            @PathVariable String userId,
            @PathVariable String packageId,
            @RequestParam(required = false) List<String> alreadyAddedPoiIds) {
        try {
            List<NearestPoiDTO> nearestPois = recommendationService.getNearestPOIs(userId, packageId, alreadyAddedPoiIds);
            return ResponseEntity.ok(nearestPois);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}