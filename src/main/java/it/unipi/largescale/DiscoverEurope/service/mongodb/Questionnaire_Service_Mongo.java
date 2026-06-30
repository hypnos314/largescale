package it.unipi.largescale.DiscoverEurope.service.mongodb;

import it.unipi.largescale.DiscoverEurope.DTO.mongodb.PackageScoreDTO;
import it.unipi.largescale.DiscoverEurope.model.mongodb.Flight;
import it.unipi.largescale.DiscoverEurope.model.mongodb.Questionnaire;
import it.unipi.largescale.DiscoverEurope.model.mongodb.embeddedQuestionnaire.QuestFeature;
import it.unipi.largescale.DiscoverEurope.model.mongodb.embeddedQuestionnaire.SuggPackage;
import it.unipi.largescale.DiscoverEurope.model.mongodb.embeddedQuestionnaire.Suggestion;
import it.unipi.largescale.DiscoverEurope.repository.mongodb.Flight_MongoInterface;
import it.unipi.largescale.DiscoverEurope.repository.mongodb.Questionnaire_MongoInterface;
import it.unipi.largescale.DiscoverEurope.repository.mongodb.TravelPackage_MongoInterface;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static it.unipi.largescale.DiscoverEurope.utils.SeasonUtils.getNextSeasonRange;

/**
 * Service that handles the questionnaire logic and calculates
 * package suggestions based on user preferences and live flight availability.
 */
@Service
public class Questionnaire_Service_Mongo {

    @Autowired
    private Questionnaire_MongoInterface questionnaireMongoInterface;
    @Autowired
    private TravelPackage_MongoInterface travelPackageMongoInterface;
    @Autowired
    private Flight_MongoInterface flightMongoInterface;

    /**
     * Saves a newly submitted questionnaire and synchronously generates matching travel suggestions.
     * @param userId the ID of the user submitting the preferences
     * @param preferences the {@link QuestFeature} data submitted
     * @return the saved {@link Questionnaire} including generated suggestions
     */
    public Questionnaire saveQuestionnaire(String userId, QuestFeature preferences) {
        Questionnaire q = new Questionnaire();
        q.setUserId(userId);
        q.setPreferences(preferences);
        q.setSubmittedAt(Instant.now());

        Suggestion suggestion = generateSuggestions(preferences);
        q.setGeneratedSuggestions(suggestion);
        questionnaireMongoInterface.save(q);
        return q;
    }

    /**
     * Updates an existing questionnaire with the user's final package selection.
     * @param questionnaireId the ID of the questionnaire
     * @param packageId the ID of the selected travel package
     * @return a status message indicating the result of the operation
     * @throws RuntimeException if an unexpected error occurs during update
     */
    public String selectPackage(String questionnaireId, String packageId) {
        try {
            if (!ObjectId.isValid(packageId))
                return "Invalid Package ID format";
            var questionnaireOpt = questionnaireMongoInterface.findById(questionnaireId);
            if (questionnaireOpt.isEmpty())
                return "Questionnaire not found";
            Questionnaire questionnaire = questionnaireOpt.get();
            if (questionnaire.getGeneratedSuggestions() == null) {
                return "No generated suggestions found for this questionnaire";
            }

            Suggestion suggestion = questionnaire.getGeneratedSuggestions();
            suggestion.setUserSelection(new ObjectId(packageId));
            questionnaire.setGeneratedSuggestions(suggestion);
            questionnaireMongoInterface.save(questionnaire);
            return "Selection updated successfully";
        } catch (Exception e) {
            throw new RuntimeException("Error updating package selection", e);
        }
    }

    /**
     * Retrieves all questionnaires for a specific user.
     * @param userId the ID of the user
     * @return a list of enriched {@link Questionnaire}
     */
    public List<Questionnaire> getByUserId(String userId) {
        List<Questionnaire> questionnaires = questionnaireMongoInterface.findByUserId(userId);

        for (Questionnaire q : questionnaires) {
            if (q.getGeneratedSuggestions() != null && q.getGeneratedSuggestions().getPackages() != null) {
                String season = q.getPreferences().getPreferredSeason();
                String origin = (q.getPreferences().getDepartureCity() != null) ? q.getPreferences().getDepartureCity() : "Rome";

                for (SuggPackage sugg : q.getGeneratedSuggestions().getPackages()) {
                    var pkgOpt = travelPackageMongoInterface.findById(sugg.getPackageId().toString());
                    if (pkgOpt.isPresent()) {
                        double basePrice = pkgOpt.get().getPrice();
                        String destCity = pkgOpt.get().getCity();
                        enrichWithLiveFlights(sugg, origin, destCity, season, basePrice);
                    }
                }
            }
        }
        return questionnaires;
    }

    private Suggestion generateSuggestions(QuestFeature preferences) {
        String originCity = (preferences.getDepartureCity() != null) ? preferences.getDepartureCity() : "Rome";

        List<String> reachableCities = flightMongoInterface.findReachableDestinations(originCity)
                .stream()
                .map(f -> f.getArrival().getCity())
                .distinct()
                .collect(Collectors.toList());

        if (reachableCities.isEmpty()) {
            Suggestion s = new Suggestion();
            s.setSuggestedAt(Instant.now());
            s.setPackages(List.of()); // Ritorna lista vuota
            return s;
        }

        List<PackageScoreDTO> top3 = travelPackageMongoInterface.findTop3BestMatches(preferences, reachableCities);
        Instant now = Instant.now();
        String season = preferences.getPreferredSeason();

        List<SuggPackage> finalPackages = top3.stream().map(dto -> {
            SuggPackage sugg = new SuggPackage();
            sugg.setPackageId(new ObjectId(dto.getId()));
            sugg.setMatchScore(dto.getScore());

            if (preferences.isSurprise() && dto.getDestinationCity() != null) {
                sugg.setName(dto.getTitle().replace(dto.getDestinationCity(), "a Mystery Destination"));
            } else {
                sugg.setName(dto.getTitle());
            }
            enrichWithLiveFlights(sugg, originCity, dto.getDestinationCity(), season, dto.getPrice());
            return sugg;
        }).collect(Collectors.toList());

        Suggestion s = new Suggestion(); s.setSuggestedAt(now); s.setPackages(finalPackages);
        return s;
    }

    private void enrichWithLiveFlights(SuggPackage sugg, String origin, String dest, String season, double basePrice) {
        int defaultDuration = 7;
        double roundedBasePrice = Math.round(basePrice * 100.0) / 100.0;
        sugg.setBasePrice(roundedBasePrice);
        if (dest == null || dest.isEmpty() || origin.equalsIgnoreCase(dest)) {
            sugg.setDurationDays(defaultDuration);
            sugg.setPrice(roundedBasePrice);
            return;
        }

        Instant now = Instant.now();
        Instant twoWeeksFromNow = now.plus(14, ChronoUnit.DAYS);
        Instant[] range = getNextSeasonRange(season);

        Instant effectiveQueryStart = (range[0].isAfter(twoWeeksFromNow)) ? range[0] : twoWeeksFromNow;

        Optional<Flight> outOpt = Optional.empty();
        Optional<Flight> retOpt = Optional.empty();

        List<Flight> outbounds = flightMongoInterface.findOutboundFlights(origin, dest, effectiveQueryStart, range[1]);
        if (!outbounds.isEmpty()) {
            outOpt = Optional.of(outbounds.get(0));
        } else {
            List<Flight> fallbackOutbounds = flightMongoInterface.findAvailableOutbounds(origin, dest, twoWeeksFromNow);
            if (!fallbackOutbounds.isEmpty()) {
                outOpt = Optional.of(fallbackOutbounds.get(0));
            }
        }

        if (outOpt.isPresent()) {
            Instant decolloAndata = outOpt.get().getDeparture().getAt();

            Instant minReturnOk = decolloAndata.plus(2, ChronoUnit.DAYS);
            Instant maxReturnOk = decolloAndata.plus(16, ChronoUnit.DAYS);

            List<Flight> returns = flightMongoInterface.findReturnFlights(dest, origin, decolloAndata);
            retOpt = returns.stream()
                    .filter(f -> !f.getDeparture().getAt().isBefore(minReturnOk) && !f.getDeparture().getAt().isAfter(maxReturnOk))
                    .findFirst();
            if (retOpt.isEmpty() && !returns.isEmpty()) {
                retOpt = Optional.of(returns.get(0));
            }
        }

        if (outOpt.isPresent() && retOpt.isPresent()) {
            Flight andata = outOpt.get();
            Flight ritorno = retOpt.get();

            sugg.setOutboundFlightId(andata.getId());
            sugg.setReturnFlightId(ritorno.getId());
            sugg.setDepartureDate(andata.getDeparture().getAt());
            sugg.setReturnDate(ritorno.getArrival().getAt());

            long days = ChronoUnit.DAYS.between(andata.getDeparture().getAt(), ritorno.getArrival().getAt());
            sugg.setDurationDays((int) Math.max(1, days));
            double rawTotal = basePrice + andata.getPrice() + ritorno.getPrice();
            sugg.setPrice(Math.round(rawTotal * 100.0) / 100.0);
        } else {
            sugg.setDurationDays(defaultDuration);
            sugg.setPrice(Math.round(basePrice * 100.0) / 100.0);
        }
    }
}