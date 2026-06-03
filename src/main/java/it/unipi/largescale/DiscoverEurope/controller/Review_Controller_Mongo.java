package it.unipi.largescale.DiscoverEurope.controller;

import it.unipi.largescale.DiscoverEurope.DTO.ReviewRequestDTO;
import it.unipi.largescale.DiscoverEurope.service.Review_Service_Mongo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/reviews")
public class Review_Controller_Mongo {
    @Autowired
    private Review_Service_Mongo reviewServiceMongo;

    @PostMapping
    public ResponseEntity<String> submitReview(@RequestBody ReviewRequestDTO request) {
        try {
            // Validazione base di sicurezza (evitiamo recensioni vuote o senza pacchetto)
            if (request.getPackageId() == null || request.getUserId() == null || request.getRating() < 1 || request.getRating() > 5) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid review data provided");
            }
            // Chiamiamo il metodo del Service che salva su DB e aggiorna il TravelPackage
            String result = reviewServiceMongo.createReview(
                    request.getUserId(),
                    request.getUserName(),
                    request.getPackageId(),
                    request.getPackageTitle(),
                    request.getRating(),
                    request.getComment(),
                    request.isSurprise()
            );
            // Se tutto va bene, restituiamo 201 Created (Codice standard per la creazione di risorse)
            return ResponseEntity.status(HttpStatus.CREATED).body(result);
        } catch (Exception e) {
            // Se il pacchetto non esiste o c'è un errore del DB, restituiamo 500
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error while submitting the review");
        }
    }
}
