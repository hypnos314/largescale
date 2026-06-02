package it.unipi.largescale.DiscoverEurope.controller;

import it.unipi.largescale.DiscoverEurope.DTO.QuestionnaireRequestDTO;
import it.unipi.largescale.DiscoverEurope.model.Questionnaire;
import it.unipi.largescale.DiscoverEurope.service.Questionnaire_Service_Mongo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/questionnaires")
public class Questionnaire_Controller_Mongo {
    @Autowired
    private Questionnaire_Service_Mongo questionnaireService;

    @PostMapping("/user/submit")
    public ResponseEntity<Questionnaire> submitQuestionnaire(@RequestBody QuestionnaireRequestDTO request) {
        Questionnaire savedQuestionnaire = questionnaireService.saveQuestionnaire(
                request.getUserId(),
                request.getPreferences()
        );
        return new ResponseEntity<>(savedQuestionnaire, HttpStatus.CREATED);
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Questionnaire>> getUserQuestionnaires(@PathVariable String userId) {
        // Chiede al service la lista dei questionari
        List<Questionnaire> questionnaires = questionnaireService.getByUserId(userId);

        // Restituisce la lista e lo status code 200 (OK)
        return ResponseEntity.ok(questionnaires);
    }

    @PutMapping("/{id}/select-package")
    public ResponseEntity<String> selectPackage(
            @PathVariable String id, // Cattura l'ID del questionario dall'URL
            @RequestBody SelectPackageDTO request // Riceve il packageId nel Body
    ) {
        try {
            String result = questionnaireService.selectPackage(id, request.getPackageId());

            if (result.equals("Selection updated successfully")) {
                return ResponseEntity.ok(result); // 200 OK
            } else if (result.equals("Questionnaire not found")) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(result); // 404
            } else {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(result); // 400
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Internal server error");
        }
    }
}


