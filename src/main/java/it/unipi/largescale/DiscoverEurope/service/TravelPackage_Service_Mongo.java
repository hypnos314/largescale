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
        System.out.println("--- DEBUG SUGGERIMENTI ---");
        System.out.println("1. Cerco questionario per User ID: " + userId);

        Optional<Questionnaire> latestQuestionnaireOpt = questionnaireMongoInterface
                .findTopByUserIdOrderBySubmittedAtDesc(userId);

        if (latestQuestionnaireOpt.isEmpty()) {
            System.out.println(" BLOCCO 1: Nessun questionario trovato per questo utente nel DB!");
            return new ArrayList<>();
        }

        Questionnaire latestQuestionnaire = latestQuestionnaireOpt.get();
        System.out.println(" Questionario trovato! ID: " + latestQuestionnaire.getId());

        if (latestQuestionnaire.getGeneratedSuggestions() == null) {
            System.out.println(" BLOCCO 2: Il questionario è stato trovato, ma 'generatedSuggestions' è NULL. (Problema di mappatura tra Java e Mongo)");
            return new ArrayList<>();
        }

        List<String> suggestedIds = latestQuestionnaire.getGeneratedSuggestions().getPackages()
                .stream()
                .map(sugg -> sugg.getPackageId().toString())
                .collect(Collectors.toList());

        System.out.println(" ID Pacchetti estratti: " + suggestedIds);

        List<TravelPackage> results = new ArrayList<>();
        travelPackageMongoInterface.findAllById(suggestedIds).forEach(results::add);

        System.out.println(" Pacchetti reali trovati in TravelPackages: " + results.size());
        System.out.println("--------------------------");

        return results;
    }
}
