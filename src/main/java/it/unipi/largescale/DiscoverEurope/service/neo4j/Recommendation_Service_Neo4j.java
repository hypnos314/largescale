package it.unipi.largescale.DiscoverEurope.service.neo4j;

import it.unipi.largescale.DiscoverEurope.DTO.neo4j.*;
import it.unipi.largescale.DiscoverEurope.repository.neo4j.Recommendation_Neo4jInterface;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.List;

/**
 * Recommendation service leveraging Neo4j graph algorithms to personalize travel suggestions.
 */
@Service
public class Recommendation_Service_Neo4j {

    @Autowired
    private Recommendation_Neo4jInterface recommendationRepository;

    /**
     * Retrieves globally trending packages based on recent high ratings.
     * @return a list of {@link PopularPackageDTO}
     */
    public List<PopularPackageDTO> getPopularPackages() {
        return recommendationRepository.getPopularPackages();
    }

    /**
     * Provides personalized recommendations using content-based filtering based on user interests.
     * @param userId the target user ID
     * @return a list of {@link RecommendedPackageDTO}
     */
    public List<RecommendedPackageDTO> getForYouByInterests(String userId) {
        return recommendationRepository.getRecommendationsByInterests(userId);
    }

    /**
     * Provides personalized recommendations using collaborative filtering based on user age demographic.
     * @param userId the target user ID
     * @return a list of {@link AgeRecommendationDTO}
     */
    public List<AgeRecommendationDTO> getRecommendationsByAge(String userId) {
        return recommendationRepository.getRecommendationsByAge(userId);
    }

    /**
     * Calculates spatial recommendations for Points of Interest near a booked hotel.
     * @param userId the ID of the user
     * @param packageId the ID of the booked package
     * @param alreadyAddedPoiIds list of POIs to exclude from suggestions
     * @return a list of {@link NearestPoiDTO}
     */
    public List<NearestPoiDTO> getNearestPOIs(String userId, String packageId, List<String> alreadyAddedPoiIds) {
        if (alreadyAddedPoiIds == null) {
            alreadyAddedPoiIds = new ArrayList<>();
        }
        return recommendationRepository.getNearestPOIs(userId, packageId, alreadyAddedPoiIds);
    }
}