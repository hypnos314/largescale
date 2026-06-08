package it.unipi.largescale.DiscoverEurope.repository;

import it.unipi.largescale.DiscoverEurope.DTO.PackageScoreDTO;
import it.unipi.largescale.DiscoverEurope.model.embeddedPackage.Feature;

import java.util.List;

public interface TravelPackage_Custom_MongoInterface {
    List<PackageScoreDTO> findTop3BestMatches(Feature preferences);
}
