package it.unipi.largescale.DiscoverEurope.controller;
@RestController
@RequestMapping("/api/questionnaires")

public class Questionnaire_Controller_Mongo {
}
@Autowired
    private Questionnaire_Service_Mongo questionnaireService;

List<Questionnaire> userQuestionnaires = questionnaireService.getByUserId(userId);

@PostMapping

  public ResponseEntity<Questionnaire> submitQuestionnaire(@RequestBody QuestionnaireRequest request) {
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
}
