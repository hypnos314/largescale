package it.unipi.largescale.DiscoverEurope.service;

import it.unipi.largescale.DiscoverEurope.model.Review;
import it.unipi.largescale.DiscoverEurope.model.TravelPackage;
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
                // ==========================================
                // 1. SALVATAGGIO NELLA COLLEZIONE 'REVIEWS'
                // ==========================================
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

                // ==========================================
                // 2. SINCRONIZZAZIONE CON 'TRAVEL_PACKAGES'
                // ==========================================
                syncPackageReviewsAndRating(packageId);

                return "Review submitted successfully";

            } catch (Exception e) {
                throw new RuntimeException("Error while submitting the review", e);
            }
        }

        // Metodo privato di supporto per tenere il codice pulito
        private void syncPackageReviewsAndRating(String packageId) {
            // Recupera il pacchetto da aggiornare
            TravelPackage pkg = travelPackageRepository.findById(packageId)
                    .orElseThrow(() -> new RuntimeException("Package not found"));

            // Recupera TUTTE le recensioni di questo pacchetto (già ordinate dalla più recente)
            List<Review> allPackageReviews = reviewRepository.findByPackageIdOrderByCreatedAtDesc(packageId);

            // A. Calcolo delle ultime 3 recensioni
            List<ReviewPackage> latest3 = allPackageReviews.stream()
                    .limit(3)
                    .map(rev -> {
                        // Mappa l'oggetto Review globale nell'oggetto ridotto ReviewPackage che sta nel TravelPackage
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

            // B. (Bonus) Aggiornamento automatico della media recensioni (Rating Summary)
            // Visto che nel JSON hai "rating_summary", è fondamentale aggiornare anche quello!
            double newAverage = allPackageReviews.stream()
                    .mapToDouble(Review::getRating)
                    .average()
                    .orElse(0.0);

            // Arrotonda a due decimali (es. 4.02)
            newAverage = Math.round(newAverage * 100.0) / 100.0;

            pkg.getRatingSummary().setAverage(newAverage);
            pkg.getRatingSummary().setTotalReviews(allPackageReviews.size());

            // Salva il pacchetto sovrascrivendo i vecchi dati con quelli aggiornati
            travelPackageRepository.save(pkg);
        }
}
