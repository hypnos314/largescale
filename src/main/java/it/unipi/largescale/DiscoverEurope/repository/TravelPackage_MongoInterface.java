package it.unipi.largescale.DiscoverEurope.repository;

import it.unipi.largescale.DiscoverEurope.model.TravelPackage;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface TravelPackage_MongoInterface extends MongoRepository<TravelPackage, String> {
    // Estrae tutti i pacchetti per processarli in memoria tramite lo scoring algorithm
    List<TravelPackage> findAll();
}
