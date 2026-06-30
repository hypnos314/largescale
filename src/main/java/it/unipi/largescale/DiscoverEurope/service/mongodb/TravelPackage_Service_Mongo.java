package it.unipi.largescale.DiscoverEurope.service.mongodb;

import it.unipi.largescale.DiscoverEurope.model.mongodb.Questionnaire;
import it.unipi.largescale.DiscoverEurope.model.mongodb.TravelPackage;
import it.unipi.largescale.DiscoverEurope.repository.mongodb.Questionnaire_MongoInterface;
import it.unipi.largescale.DiscoverEurope.repository.mongodb.TravelPackage_MongoInterface;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Service responsible for retrieving travel package details.
 */
@Service
public class TravelPackage_Service_Mongo {

    @Autowired
    private TravelPackage_MongoInterface travelPackageMongoInterface;
    @Autowired
    private Questionnaire_MongoInterface questionnaireMongoInterface;

    /**
     * Retrieves the complete details of a specific travel package.
     * @param packageId the ID of the package
     * @return the {@link TravelPackage} entity
     * @throws RuntimeException if the package is not found
     */
    public TravelPackage getPackageDetails(String packageId){
        return travelPackageMongoInterface.findById(packageId)
                .orElseThrow(()-> new RuntimeException("Travel package not found: " + packageId));
    }

    /**
     * Extracts and retrieves the full travel package entities that were suggested
     * in the user's most recently submitted questionnaire.
     * @param userId the ID of the user
     * @return a list of recommended {@link TravelPackage}
     */
    public List<TravelPackage> getSuggestedPackages(String userId){

        Optional<Questionnaire> latestQuestionnaireOpt = questionnaireMongoInterface
                .findTopByUserIdOrderBySubmittedAtDesc(userId);

        if (latestQuestionnaireOpt.isEmpty() || latestQuestionnaireOpt.get().getGeneratedSuggestions() == null) {
            return new ArrayList<>();
        }

        Questionnaire latestQuestionnaire = latestQuestionnaireOpt.get();

        List<String> suggestedIds = latestQuestionnaire.getGeneratedSuggestions().getPackages()
                .stream()
                .map(sugg -> sugg.getPackageId().toString())
                .collect(Collectors.toList());

        List<TravelPackage> results = new ArrayList<>();
        travelPackageMongoInterface.findAllById(suggestedIds).forEach(results::add);
        return results;
    }
}
