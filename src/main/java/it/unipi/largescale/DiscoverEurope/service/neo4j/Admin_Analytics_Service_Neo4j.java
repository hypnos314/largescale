package it.unipi.largescale.DiscoverEurope.service.neo4j;

import it.unipi.largescale.DiscoverEurope.DTO.neo4j.*;
import it.unipi.largescale.DiscoverEurope.repository.neo4j.Admin_Analytics_Neo4jInterface;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

/**
 * Service executing administrative queries on the Neo4j graph database.
 */
@Service
public class Admin_Analytics_Service_Neo4j {

    @Autowired
    private Admin_Analytics_Neo4jInterface adminNeo4jRepository;

    /**
     * Retrieves top travel destinations categorized by user age demographics.
     * @return a list of {@link PopularDestinationsDTO}
     */
    public List<PopularDestinationsDTO> getPopularDestinationsByAgeGroup() {
        return adminNeo4jRepository.getPopularDestinationsByAgeGroup();
    }
}