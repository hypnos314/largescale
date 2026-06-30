package it.unipi.largescale.DiscoverEurope.repository.mongodb;

import it.unipi.largescale.DiscoverEurope.model.mongodb.Flight;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;

/**
 * Repository for managing Flight documents in MongoDB.
 * Handles spatio-temporal queries to find available flight routes and schedules.
 */
@Repository
public interface Flight_MongoInterface extends MongoRepository<Flight, String> {

    /**
     * Pre-fetches all reachable destinations from a specific departure city.
     * @param departureCity the origin city
     * @return a list of flights containing only the arrival city names
     */
    @Query(value = "{ 'departure.city': ?0 }", fields = "{ 'arrival.city': 1, '_id': 0 }")
    List<Flight> findReachableDestinations(String departureCity);

    /**
     * Finds outbound flights within a specific seasonal time frame.
     * @param originCity departure city
     * @param destCity arrival city
     * @param seasonStart start of the acceptable time window
     * @param seasonEnd end of the acceptable time window
     * @return list of available outbound flights
     */
    @Query("{ 'departure.city': ?0, 'arrival.city': ?1, 'departure.at': { $gte: ?2, $lte: ?3 } }")
    List<Flight> findOutboundFlights(String originCity, String destCity, Instant seasonStart, Instant seasonEnd);

    /**
     * Fallback query to find all future outbound flights ordered chronologically.
     */
    @Query(value = "{ 'departure.city': ?0, 'arrival.city': ?1, 'departure.at': { $gt: ?2 } }", sort = "{ 'departure.at': 1 }")
    List<Flight> findAvailableOutbounds(String originCity, String destCity, Instant now);

    /**
     * Finds return flights scheduled after the arrival of the outbound flight.
     */
    @Query(value = "{ 'departure.city': ?0, 'arrival.city': ?1, 'departure.at': { $gt: ?2 } }", sort = "{ 'departure.at': 1 }")
    List<Flight> findReturnFlights(String destCity, String originCity, Instant outboundArrivalTime);
}