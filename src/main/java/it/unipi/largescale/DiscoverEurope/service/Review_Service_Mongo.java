package it.unipi.largescale.DiscoverEurope.service;

import it.unipi.largescale.DiscoverEurope.model.Review;
import it.unipi.largescale.DiscoverEurope.model.TravelPackage;
import it.unipi.largescale.DiscoverEurope.model.embeddedPackage.Rating;
import it.unipi.largescale.DiscoverEurope.model.embeddedPackage.ReviewPackage;
import it.unipi.largescale.DiscoverEurope.repository.Review_MongoInterface;
import it.unipi.largescale.DiscoverEurope.repository.TravelPackage_MongoInterface;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class Review_Service_Mongo {
        @Autowired
        private Review_MongoInterface reviewRepository;

        @Autowired
        private TravelPackage_MongoInterface travelPackageRepository;

        // Funzione chiamata dal Controller quando l'utente invia una recensione
        public String createReview(String userId, String userName, String packageId, String packageTitle, double rating, String comment, boolean isSurprise) {
            try {

                Review newReview = new Review();
                newReview.setUserId(userId);
                newReview.setUserName(userName);
                newReview.setPackageId(packageId);
                newReview.setPackageTitle(packageTitle);
                newReview.setRating(rating);
                newReview.setComment(comment);
                newReview.setCreatedAt(Instant.now());
                newReview.setSurprise(isSurprise);

                reviewRepository.save(newReview);

                syncPackageReviewsAndRating(packageId, rating);

                return "Review submitted successfully";

            } catch (Exception e) {
                throw new RuntimeException("Error while submitting the review", e);
            }
        }

        // Metodo privato di supporto per tenere il codice pulito
        private void syncPackageReviewsAndRating(String packageId, double newRating) {
            TravelPackage pkg = travelPackageRepository.findById(packageId)
                    .orElseThrow(() -> new RuntimeException("Package not found"));

            // Recuperiamo SOLO le ultime 3 recensioni
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

            // Aggiornamento della Media
            // Controllo di sicurezza: se il pacchetto non aveva mai ricevuto recensioni prima
            if (pkg.getRatingSummary() == null) {
                pkg.setRatingSummary(new Rating());
                pkg.getRatingSummary().setTotalReviews(0);
                pkg.getRatingSummary().setAverage(0.0);
            }

            int oldCount = pkg.getRatingSummary().getTotalReviews();
            double oldAverage = pkg.getRatingSummary().getAverage();

            // Formula matematica per la nuova media: ((Vecchia Media * Vecchio Conteggio) + Nuovo Voto) / Nuovo Conteggio
            double newAverage = ((oldAverage * oldCount) + newRating) / (oldCount + 1);
            newAverage = Math.round(newAverage * 100.0) / 100.0;

            pkg.getRatingSummary().setAverage(newAverage);
            pkg.getRatingSummary().setTotalReviews(oldCount + 1);

            // Salviamo il pacchetto aggiornato
            travelPackageRepository.save(pkg);
        }

        // List<Review> getLandingPageReviews(); Per la front page
}
