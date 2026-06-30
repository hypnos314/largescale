package it.unipi.largescale.DiscoverEurope.repository.mongodb;

import it.unipi.largescale.DiscoverEurope.DTO.mongodb.SharedHotelDTO;
import it.unipi.largescale.DiscoverEurope.model.mongodb.TravelPackage;
import org.springframework.data.mongodb.repository.Aggregation;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Main Repository for TravelPackages.
 */
@Repository
public interface TravelPackage_MongoInterface extends MongoRepository<TravelPackage, String>, TravelPackage_Custom_MongoInterface {
    List<TravelPackage> findAll();

    /**
     * Identifies shared hotels among
     * the highest-rated travel packages within a specific destination.
     * @param city the target destination city
     * @return list of shared hotels and their frequency
     */
    @Aggregation(pipeline = {
            "{ $match: { 'city': ?0, 'rating_summary.average': { $gte: 4.0 } } }",
            "{ $group: { " +
                    "      _id: '$hotel.hotel_info.name', " +
                    "      frequency: { $sum: 1 }, " +
                    "      involved_packages: { $push: { packageId: '$_id', packageName: '$title' } } " +
                    "} }",
            "{ $match: { frequency: { $gt: 1 } } }",
            "{ $sort: { frequency: -1 } }",
            "{ $limit: 10 }"
    })
    List<SharedHotelDTO> getSharedHotelTopPackages(String city);
}
