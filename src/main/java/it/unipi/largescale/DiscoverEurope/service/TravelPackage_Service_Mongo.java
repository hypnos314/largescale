package it.unipi.largescale.DiscoverEurope.service;

import it.unipi.largescale.DiscoverEurope.model.Questionnaire;
import it.unipi.largescale.DiscoverEurope.model.TravelPackage;
import it.unipi.largescale.DiscoverEurope.model.User;
import it.unipi.largescale.DiscoverEurope.repository.Questionnaire_MongoInterface;
import it.unipi.largescale.DiscoverEurope.repository.TravelPackage_MongoInterface;
import it.unipi.largescale.DiscoverEurope.repository.User_MongoInterface;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class TravelPackage_Service_Mongo {
    @Autowired
    private TravelPackage_MongoInterface travelPackageMongoInterface;
    @Autowired
    private Questionnaire_MongoInterface questionnaireMongoInterface;

    //public List<TravelPackage> getRecommendedPackages(String userId); serve interfaccia Neo4j

    //List<TravelPackage> getPopularPackages(); serve l'interfaccia di Neo4j

    public TravelPackage getPackageDetails(String packageId){
        return travelPackageMongoInterface.findById(packageId)
                .orElseThrow(()-> new RuntimeException("Travel package not found: " + packageId));
    }

    //prende i dettagli dei pacchetti suggeriti dell'ultimo questionario
    public List<TravelPackage> getSuggestedPackages(String userId){

        // Cerchiamo l'ultimo questionario compilato dall'utente nella collection separata
        Optional<Questionnaire> latestQuestionnaireOpt = questionnaireMongoInterface
                .findTopByUserIdOrderBySubmittedAtDesc(userId);

        // Se non ha mai fatto un questionario (o se per qualche motivo non ha suggerimenti), lista vuota
        if (latestQuestionnaireOpt.isEmpty() || latestQuestionnaireOpt.get().getGeneratedSuggestions() == null) {
            return new ArrayList<>();
        }

        Questionnaire latestQuestionnaire = latestQuestionnaireOpt.get();

        // Estraiamo gli ID dei pacchetti vincenti generati dalla nostra Aggregation Pipeline
        List<String> suggestedIds = latestQuestionnaire.getGeneratedSuggestions().getPackages()
                .stream()
                .map(sugg -> sugg.getPackageId().toString())
                .collect(Collectors.toList());

        // Scarichiamo le schede complete dei pacchetti da MongoDB
        List<TravelPackage> results = new ArrayList<>();
        travelPackageMongoInterface.findAllById(suggestedIds).forEach(results::add); //prende i pacchetti generati, li mette nella lista results
        return results;
    }
}
