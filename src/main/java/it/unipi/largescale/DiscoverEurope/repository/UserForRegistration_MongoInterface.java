package it.unipi.largescale.DiscoverEurope.repository;

import it.unipi.largescale.DiscoverEurope.model.UserForRegistration;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface UserForRegistration_MongoInterface  extends MongoRepository<UserForRegistration, String> {

}
