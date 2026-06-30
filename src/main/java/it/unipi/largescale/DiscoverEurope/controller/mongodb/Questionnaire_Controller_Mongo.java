package it.unipi.largescale.DiscoverEurope.controller.mongodb;

import it.unipi.largescale.DiscoverEurope.DTO.mongodb.QuestionnaireRequestDTO;
import it.unipi.largescale.DiscoverEurope.DTO.mongodb.SelectPackageDTO;
import it.unipi.largescale.DiscoverEurope.model.mongodb.Questionnaire;
import it.unipi.largescale.DiscoverEurope.service.mongodb.Questionnaire_Service_Mongo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Questionnaire's REST Controller.
 */
@RestController
@RequestMapping("/api/questionnaires")
public class Questionnaire_Controller_Mongo {
    @Autowired
    private Questionnaire_Service_Mongo questionnaireService;

    /**
     * Submits a new user questionnaire containing travel preferences.
     * @param request the questionnaire data, including the userId and preferences.
     * @return a {@link ResponseEntity} containing the saved {@link Questionnaire} and CREATED status.
     */
    @PostMapping("/user/submit")
    public ResponseEntity<Questionnaire> submitQuestionnaire(@RequestBody QuestionnaireRequestDTO request) {
        Questionnaire savedQuestionnaire = questionnaireService.saveQuestionnaire(
                request.getUserId(),
                request.getPreferences()
        );
        return new ResponseEntity<>(savedQuestionnaire, HttpStatus.CREATED);
    }

    /**
     * Retrieves all questionnaires associated with a specific user.
     * @param userId the unique identifier of the user.
     * @return a {@link ResponseEntity} containing a list of {@link Questionnaire} objects.
     */
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Questionnaire>> getUserQuestionnaires(@PathVariable String userId) {
        List<Questionnaire> questionnaires = questionnaireService.getByUserId(userId);
        return ResponseEntity.ok(questionnaires);
    }

    /**
     * Updates a specific questionnaire with the user's selected travel package.
     * @param id the ID of the questionnaire.
     * @param request contains the selected packageId.
     * @return a {@link ResponseEntity} with the result status message.
     */
    @PutMapping("/{id}/select-package")
    public ResponseEntity<String> selectPackage(
            @PathVariable String id,
            @RequestBody SelectPackageDTO request
    ) {
        try {
            String result = questionnaireService.selectPackage(id, request.getPackageId());
            if (result.equals("Selection updated successfully")) {
                return ResponseEntity.ok(result);
            } else if (result.equals("Questionnaire not found")) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(result);
            } else {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(result);
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Internal server error");
        }
    }
}


