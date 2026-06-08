package it.unipi.largescale.DiscoverEurope.controller;

import it.unipi.largescale.DiscoverEurope.model.TravelPackage;
import it.unipi.largescale.DiscoverEurope.service.TravelPackage_Service_Mongo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/packages")
public class TravelPackage_Controller_Mongo {
    @Autowired
    private TravelPackage_Service_Mongo travelPackageService;

    // 1. Dettagli completi del pacchetto (Schermata: Royal London & Sacred Treasures)
    @GetMapping("/{packageId}")
    public ResponseEntity<?> getPackageDetails(@PathVariable String packageId) {
        try {
            TravelPackage travelPackage = travelPackageService.getPackageDetails(packageId);
            return ResponseEntity.ok(travelPackage);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    // 2. Risultati del Questionario in chiaro (Schermata: Your trip is waiting for you!)
    @GetMapping("/suggested/{userId}")
    public ResponseEntity<List<TravelPackage>> getSuggestedPackages(@PathVariable String userId) {
        try {
            // Usa la tua funzione che legge il questionario e scarica i pacchetti
            List<TravelPackage> packages = travelPackageService.getSuggestedPackages(userId);
            return ResponseEntity.ok(packages);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }


    // ==========================================
    // ENDPOINT DA COMPLETARE
    // ==========================================

    // 4. Sezione "For You" Home (Futuro Neo4j)
    @GetMapping("/for-you/{userId}")
    public ResponseEntity<List<TravelPackage>> getForYouPackages(@PathVariable String userId) {
        /* TODO: Implementare query Neo4j basata su interessi ed età
         */
        return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).build(); // 501
    }

    // 5. Sezione "Popular" Home (Futuro Neo4j)
    @GetMapping("/popular")
    public ResponseEntity<List<TravelPackage>> getPopularPackages() {
        /* TODO: Implementare query Neo4j basata sui pacchetti più acquistati/votati
         */
        return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).build(); // 501
    }
}
