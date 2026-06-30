package it.unipi.largescale.DiscoverEurope.controller.mongodb;

import it.unipi.largescale.DiscoverEurope.DTO.mongodb.ReviewRequestDTO;
import it.unipi.largescale.DiscoverEurope.model.mongodb.Review;
import it.unipi.largescale.DiscoverEurope.service.mongodb.Review_Service_Mongo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Review's REST Controller.
 */
@RestController
@RequestMapping("/api/reviews")
public class Review_Controller_Mongo {
    @Autowired
    private Review_Service_Mongo reviewServiceMongo;

    /**
     * Submits a new review for a completed travel package.
     * @param request the review details.
     * @return a {@link ResponseEntity} with a success message and CREATED status.
     */
    @PostMapping
    public ResponseEntity<String> submitReview(@RequestBody ReviewRequestDTO request) {
        try {
            if (request.getPackageId() == null || request.getUserId() == null || request.getRating() < 1 || request.getRating() > 5) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid review data provided");
            }
            String result = reviewServiceMongo.createReview(
                    request.getUserId(),
                    request.getUserName(),
                    request.getPackageId(),
                    request.getPackageTitle(),
                    request.getRating(),
                    request.getComment(),
                    request.isSurprise()
            );
            return ResponseEntity.status(HttpStatus.CREATED).body(result);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error while submitting the review");
        }
    }

    /**
     * Retrieves all approved reviews associated with a specific travel package.
     * @param packageId the unique identifier of the travel package.
     * @return a {@link ResponseEntity} containing a list of {@link Review} objects.
     */
    @GetMapping("/package/{packageId}")
    public ResponseEntity<List<Review>> getReviewsByPackage(@PathVariable String packageId) {
        try {
            List<Review> reviews = reviewServiceMongo.getReviewsByPackageId(packageId);
            return ResponseEntity.ok(reviews);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
