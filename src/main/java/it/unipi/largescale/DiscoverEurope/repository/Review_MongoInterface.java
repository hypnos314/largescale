package it.unipi.largescale.DiscoverEurope.repository;

import it.unipi.largescale.DiscoverEurope.model.Review;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface Review_MongoInterface extends MongoRepository<Review, String> {
    //List<Review> findTop3ByOrderByCreatedAtDesc(); per la front page
    List<Review> findByPackageId(String packageId); //serve per il pulsante load more nella pagina dei dettagli del pacchetto
    List<Review> findByPackageIdOrderByCreatedAtDesc(String packageId); //carica recensioni di un pacchetto in ordine dal più recente
}
