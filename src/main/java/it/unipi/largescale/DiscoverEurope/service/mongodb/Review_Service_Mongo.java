package it.unipi.largescale.DiscoverEurope.service.mongodb;

import it.unipi.largescale.DiscoverEurope.event.Task;
import it.unipi.largescale.DiscoverEurope.event.TaskToDo;
import it.unipi.largescale.DiscoverEurope.model.mongodb.Review;
import it.unipi.largescale.DiscoverEurope.model.mongodb.TravelPackage;
import it.unipi.largescale.DiscoverEurope.model.mongodb.embeddedPackage.Rating;
import it.unipi.largescale.DiscoverEurope.model.mongodb.embeddedPackage.ReviewPackage;
import it.unipi.largescale.DiscoverEurope.repository.mongodb.Review_MongoInterface;
import it.unipi.largescale.DiscoverEurope.repository.mongodb.TravelPackage_MongoInterface;
import it.unipi.largescale.DiscoverEurope.service.Neo4jSyncManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Service managing the submission of user reviews and updating package rating aggregates.
 */
@Service
public class Review_Service_Mongo {

    @Autowired
    private Review_MongoInterface reviewRepository;
    @Autowired
    private TravelPackage_MongoInterface travelPackageRepository;
    @Autowired
    private Neo4jSyncManager syncManager;

    /**
     * Submits a new review, updates the package's average rating,
     * and triggers an asynchronous task to update the Neo4j graph.
     * @param userId the ID of the reviewing user
     * @param userName the name of the user
     * @param packageId the ID of the reviewed package
     * @param packageTitle the title of the package
     * @param rating the numeric score given
     * @param comment the text feedback
     * @param isSurprise true if the package was a surprise trip
     * @return a success confirmation message
     * @throws RuntimeException if an error occurs during saving or graph syncing
     */
    public String createReview(String userId, String userName, String packageId, String packageTitle, double rating, String comment, boolean isSurprise) {
        try {
            Review newReview = new Review();
            newReview.setUserId(userId);
            newReview.setUserName(userName);
            newReview.setPackageId(packageId);
            newReview.setPackageTitle(packageTitle);
            newReview.setRating(rating);
            newReview.setComment(comment);
            newReview.setCreatedAt(Instant.now().truncatedTo(ChronoUnit.SECONDS));
            newReview.setSurprise(isSurprise);

            reviewRepository.save(newReview);
            syncPackageReviewsAndRating(packageId, rating);
            TaskToDo syncTask = new TaskToDo(
                    Task.TaskType.ADD_REVIEWED_RELATION,
                    userId,
                    packageId,
                    rating,
                    newReview.getCreatedAt().toString()
            );
            syncManager.addTask(syncTask);
            return "Review submitted successfully";
        } catch (Exception e) {
            throw new RuntimeException("Error while submitting the review", e);
        }
    }

    private void syncPackageReviewsAndRating(String packageId, double newRating) {
        TravelPackage pkg = travelPackageRepository.findById(packageId)
                .orElseThrow(() -> new RuntimeException("Package not found"));
        List<Review> top3Reviews = reviewRepository.findTop3ByPackageIdOrderByCreatedAtDesc(packageId);
        List<ReviewPackage> latest3 = top3Reviews.stream()
                .map(rev -> {
                    ReviewPackage rp = new ReviewPackage();
                    rp.setUserName(rev.getUserName());
                    rp.setPackageTitle(rev.getPackageTitle());
                    rp.setRating(rev.getRating());
                    rp.setComment(rev.getComment());
                    rp.setCreatedAt(rev.getCreatedAt());
                    return rp;
                })
                .collect(Collectors.toList());
        pkg.setLatestReviews(latest3);

        if (pkg.getRatingSummary() == null) {
            pkg.setRatingSummary(new Rating());
            pkg.getRatingSummary().setTotalReviews(0);
            pkg.getRatingSummary().setAverage(0.0);
        }

        int oldCount = pkg.getRatingSummary().getTotalReviews();
        double oldAverage = pkg.getRatingSummary().getAverage();
        double newAverage = ((oldAverage * oldCount) + newRating) / (oldCount + 1);
        newAverage = Math.round(newAverage * 100.0) / 100.0;

        pkg.getRatingSummary().setAverage(newAverage);
        pkg.getRatingSummary().setTotalReviews(oldCount + 1);

        travelPackageRepository.save(pkg);
    }

    /**
     * Retrieves all reviews associated with a specific travel package.
     * @param packageId the ID of the target package
     * @return a list of {@link Review} objects, or an empty list if none exist
     * @throws RuntimeException if a database error occurs
     */
    public List<Review> getReviewsByPackageId(String packageId) {
        try {
            List<Review> reviews = reviewRepository.findByPackageId(packageId);
            if (reviews == null) {
                return new ArrayList<>();
            }
            return reviews;
        } catch (Exception e) {
            throw new RuntimeException("Error fetching the package: " + packageId, e);
        }
    }
}
