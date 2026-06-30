package it.unipi.largescale.DiscoverEurope.controller.neo4j;

import it.unipi.largescale.DiscoverEurope.DTO.neo4j.*;
import it.unipi.largescale.DiscoverEurope.service.neo4j.Admin_Analytics_Service_Neo4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

/**
 * Admin's REST Controller.
 */
@RestController
@RequestMapping("/api/neo4j/admin/neo4j-analytics")
public class Admin_Analytics_Controller_Neo4j {

    @Autowired
    private Admin_Analytics_Service_Neo4j adminNeo4jService;

    /**
     * Retrieves the most popular travel destinations aggregated by age ranges.
     * @return a {@link ResponseEntity} containing a list of {@link PopularDestinationsDTO}
     * for each age demographic.
     */
    @GetMapping("/destinations-by-age-range")
    public ResponseEntity<List<PopularDestinationsDTO>> getDestinationsByAge() {
        return ResponseEntity.ok(adminNeo4jService.getPopularDestinationsByAgeGroup());
    }
}
