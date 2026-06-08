package it.unipi.largescale.DiscoverEurope.repository;

import it.unipi.largescale.DiscoverEurope.DTO.PackageScoreDTO;
import it.unipi.largescale.DiscoverEurope.model.embeddedPackage.Feature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationOperation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.stream.Collectors;

@Repository
public class TravelPackage_Custom_MongoInterfaceImpl implements TravelPackage_Custom_MongoInterface{
    @Autowired
    private MongoTemplate mongoTemplate;

    @Override
    public List<PackageScoreDTO> findTop3BestMatches(Feature prefs) {
        // Convertiamo gli interessi in un formato array leggibile da Mongo (evitiamo null)
        List<String> userInterests = prefs.getInterests() != null ? prefs.getInterests() : List.of(); //prendiamo gli interessi dell'utente se non vuoti
        String interestsArray = userInterests.stream().map(i -> "'" + i + "'").collect(Collectors.joining(",")); //.stream prende ogni parola,
                                                                                                                                //.map modifica il formato in nuova versione,
        String addFieldsStage = "{" +
                "$addFields: {" +
                    "isPerfectMatch: {" +
                        "$and: [" +
                            "{ $eq: ['$flightDetails.outboundFlight.departure.city', '" + prefs.getDepartureCity() + "'] }," +
                            "{ $eq: ['$features.preferredSeason', '" + prefs.getPreferredSeason() + "'] }," +
                            "{ $lte: ['$price', " + prefs.getBudgetLimit() + "] }" +
                        "]" +
                "}," +
                "score: {" +
                    "$add: [" +
                        "{ $cond: [{ $eq: ['$flightDetails.outboundFlight.departure.city', '" + prefs.getDepartureCity() + "'] }, 30, 0] }," +
                        "{ $cond: [{ $eq: ['$features.preferredSeason', '" + prefs.getPreferredSeason() + "'] }, 20, 0] }," +
                        "{ $cond: [{ $lte: ['$price', " + prefs.getBudgetLimit() + "] }, 20, 0] }," +
                        "{ $cond: [{ $eq: ['$features.travelStyle', '" + prefs.getTravelStyle() + "'] }, 15, 0] }," +
                // Calcolo Interessi: Intersezione array -> Conta -> Moltiplica per 5 -> Max 15
                        "{" +
                            "$min: [" +
                                "{ $multiply: [ { $size: { $setIntersection: [ { $ifNull: ['$features.interests', []] }, [" + interestsArray + "] ] } }, 5 ] }," +
                                "15" +
                            "]" +
                        "}" +
                    "]" +
                "}" +
            "}" +
        "}";

        AggregationOperation calculateScore = context -> context.getMappedObject(org.bson.Document.parse(addFieldsStage)); //context serve a far capire all'operazione di aggregazione
                                                                                                                                                    //i campi e le classi da usare (il contesto da prendere)
                                                                                                                                           //tra mongo e il codice java
        // PIPELINE
        Aggregation aggregation = Aggregation.newAggregation(
                calculateScore,
                Aggregation.sort(Sort.Direction.DESC, "isPerfectMatch", "score"), // Ordina: prima perfect, poi score
                Aggregation.limit(3), // PRENDI SOLO I PRIMI 3
                Aggregation.project("title", "score", "isPerfectMatch")
                        .and("city").as("destinationCity")// Restituisci solo id, titolo e punteggi
                        .and("flightDetails.outboundFlight.departure.date").as("departureDate")
                        .and("flightDetails.returnFlight.arrival.date").as("returnDate")
        );

        // ESEGUIAMO SUL DATABASE
        AggregationResults<PackageScoreDTO> results = mongoTemplate.aggregate(
                aggregation, "travel_packages", PackageScoreDTO.class
        );

        return results.getMappedResults();
    }
}
