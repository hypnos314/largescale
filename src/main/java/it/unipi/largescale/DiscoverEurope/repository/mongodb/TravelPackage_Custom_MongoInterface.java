package it.unipi.largescale.DiscoverEurope.repository.mongodb;

import it.unipi.largescale.DiscoverEurope.DTO.mongodb.PackageScoreDTO;
import it.unipi.largescale.DiscoverEurope.model.mongodb.embeddedQuestionnaire.QuestFeature;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Custom interface defining complex dynamic queries for Travel Packages.
 */
@Repository
public interface TravelPackage_Custom_MongoInterface {
    /**
     * Executes the core scoring algorithm to find the top 3 best matching packages
     * based on user preferences and reachable cities.
     */
    List<PackageScoreDTO> findTop3BestMatches(QuestFeature preferences, List<String> reachableCities);
}
