package it.unipi.largescale.DiscoverEurope.service;

import it.unipi.largescale.DiscoverEurope.model.Questionnaire;
import it.unipi.largescale.DiscoverEurope.model.TravelPackage;
import it.unipi.largescale.DiscoverEurope.model.User;
import it.unipi.largescale.DiscoverEurope.model.embeddedPackage.Feature;
import it.unipi.largescale.DiscoverEurope.model.embeddedQuestionnaire.SuggPackage;
import it.unipi.largescale.DiscoverEurope.model.embeddedQuestionnaire.Suggestion;
import it.unipi.largescale.DiscoverEurope.repository.Questionnaire_MongoInterface;
import it.unipi.largescale.DiscoverEurope.repository.TravelPackage_MongoInterface;
import it.unipi.largescale.DiscoverEurope.repository.User_MongoInterface;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Instant;
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

    private Suggestion generateSuggestions(Feature preferences){
        List<TravelPackage> allPackages = travelPackageMongoInterface.findAll();
        List<ScoredPackage> scoredCandidates = new ArrayList<>();

        for (TravelPackage pkg : allPackages) {
            int score = 0;

            // Assumiamo che la departure_city si trovi in FlightDetails (adattalo se si trova altrove)
            String pkgDepartureCity = pkg.getFlightDetails() != null ? pkg.getFlightDetails().getOutboundFlight().getDeparture().getCity(): "";
            String prefsDepartureCity = preferences.getDepartureCity();

            boolean isPerfectMatch = (
                    pkgDepartureCity.equals(prefsDepartureCity) &&
                            pkg.getFeatures().getPreferredSeason().equals(preferences.getPreferredSeason()) &&
                            pkg.getPrice() <= preferences.getBudgetLimit()
            );

            // Match per la city (30%)
            if (pkgDepartureCity.equals(prefsDepartureCity)) score += 30;

            // Match per la season (20%)
            if (pkg.getFeatures().getPreferredSeason().equals(preferences.getPreferredSeason())) score += 20;

            // Match per il budget (20%)
            if (pkg.getPrice() <= preferences.getBudgetLimit()) score += 20;

            // Match per lo style (15%)
            if (pkg.getFeatures().getTravelStyle().equals(preferences.getTravelStyle())) score += 15;

            // Match per gli interests (15%) - 5% per ogni match fino a 3
            List<String> pkgInterests = pkg.getFeatures().getInterests();
            List<String> prefInterests = preferences.getInterests();

            if (pkgInterests != null && prefInterests != null) {
                long matches = pkgInterests.stream()
                        .filter(prefInterests::contains)
                        .count();
                score += Math.min((int) matches * 5, 15);
            }

            scoredCandidates.add(new ScoredPackage(pkg, score, isPerfectMatch));
        }

        // Ordinamento: Perfect Match = true ha la priorità, a seguire il punteggio più alto
        scoredCandidates.sort(Comparator
                .comparing((ScoredPackage sp) -> sp.perfect ? 1 : 0)
                .thenComparingInt(sp -> sp.score)
                .reversed()
        );

        // Estrazione Top 3 e Mappatura su SuggPackage
        List<SuggPackage> finalPackages = scoredCandidates.stream()
                .limit(3)
                .map(sp -> {
                    SuggPackage sugg = new SuggPackage();
                    sugg.setPackageId(new ObjectId(sp.meta.getId()));
                    sugg.setMatchScore(sp.score);

                    if (preferences.isSurprise()) {
                        sugg.setName("What will it be?"); // Oscuramento sorpresa
                    } else {
                        sugg.setName(sp.meta.getTitle());
                    }
                    return sugg;
                })
                .collect(Collectors.toList());

        Suggestion suggestion = new Suggestion();
        suggestion.setSuggestedAt(Instant.now());
        suggestion.setPackages(finalPackages);

        return suggestion;


    }

    private static class ScoredPackage {
        TravelPackage meta;
        int score;
        boolean perfect;

        public ScoredPackage(TravelPackage meta, int score, boolean perfect) {
            this.meta = meta;
            this.score = score;
            this.perfect = perfect;
        }
    }
}

public List<Questionnaire> getByUserId(String userId) {
    return questionnaireMongoInterface.findByUserId(userId);
}
