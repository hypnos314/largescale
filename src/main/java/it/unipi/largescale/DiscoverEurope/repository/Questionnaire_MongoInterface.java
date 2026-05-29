package it.unipi.largescale.DiscoverEurope.repository;

import it.unipi.largescale.DiscoverEurope.model.Questionnaire;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface Questionnaire_MongoInterface extends MongoRepository<Questionnaire, String> {
    Optional<Questionnaire> findTopByUserIdOrderBySubmittedAtDesc(String userId);
}

List<Questionnaire> findByUserId(String userId);
