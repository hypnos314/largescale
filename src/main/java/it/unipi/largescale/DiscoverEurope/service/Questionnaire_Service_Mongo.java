package it.unipi.largescale.DiscoverEurope.service;

import it.unipi.largescale.DiscoverEurope.DTO.PackageScoreDTO;
import it.unipi.largescale.DiscoverEurope.model.Questionnaire;
import it.unipi.largescale.DiscoverEurope.model.TravelPackage;
import it.unipi.largescale.DiscoverEurope.model.embeddedPackage.Feature;
import it.unipi.largescale.DiscoverEurope.model.embeddedQuestionnaire.SuggPackage;
import it.unipi.largescale.DiscoverEurope.model.embeddedQuestionnaire.Suggestion;
import it.unipi.largescale.DiscoverEurope.repository.Questionnaire_MongoInterface;
import it.unipi.largescale.DiscoverEurope.repository.TravelPackage_MongoInterface;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Comparator;
import java.util.stream.Collectors;

@Service
public class Questionnaire_Service_Mongo {
    @Autowired
    private Questionnaire_MongoInterface questionnaireMongoInterface;
    @Autowired
    private TravelPackage_MongoInterface travelPackageMongoInterface;

    public Questionnaire saveQuestionnaire(String userId, Feature preferences){
        Questionnaire questionnaire = new Questionnaire();
        questionnaire.setUserId(userId);
        questionnaire.setPreferences(preferences);
        questionnaire.setSubmittedAt(Instant.now());

        Suggestion generatedSuggestions = generateSuggestions(preferences);
        questionnaire.setGeneratedSuggestions(generatedSuggestions);

        return questionnaireMongoInterface.save(questionnaire);
    }

    //Logica per calcolare il match score dei suggerimenti di pacchetti
    private Suggestion generateSuggestions(Feature preferences){
        // Chiedi a MongoDB di fare tutto il lavoro e restituirti i 3 vincitori leggeri
        List<PackageScoreDTO> top3 = travelPackageMongoInterface.findTop3BestMatches(preferences);

        // Mappali nel tuo oggetto SuggPackage gestendo la sorpresa (come avevi già fatto benissimo)
        List<SuggPackage> finalPackages = top3.stream().map(proj -> {
            SuggPackage sugg = new SuggPackage();
            sugg.setPackageId(new ObjectId(proj.getId()));
            sugg.setMatchScore(proj.getScore());
            sugg.setPrice(proj.getPrice());

            // Calcoliamo la durata (Differenza in giorni)
            if (proj.getDepartureDate() != null && proj.getReturnDate() != null) {
                long days = ChronoUnit.DAYS.between(proj.getDepartureDate(), proj.getReturnDate());
                sugg.setDurationDays((int) days);
            } else {
                sugg.setDurationDays(0); // Fallback di sicurezza
            }

            if (preferences.isSurprise()) {
                sugg.setName("What will it be?"); // Oscuramento sorpresa
            } else {
                sugg.setName(proj.getTitle());
            }
            return sugg;
        }).collect(Collectors.toList());

        // 3. Ritorna il risultato
        Suggestion suggestion = new Suggestion();
        suggestion.setSuggestedAt(Instant.now());
        suggestion.setPackages(finalPackages);

        return suggestion;
    }


    public List<Questionnaire> getByUserId(String userId) {
        return questionnaireMongoInterface.findByUserId(userId);
    }
}


