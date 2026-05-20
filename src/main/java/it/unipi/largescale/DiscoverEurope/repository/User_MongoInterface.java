package it.unipi.largescale.DiscoverEurope.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import it.unipi.largescale.DiscoverEurope.model.User;

public interface User_MongoInterface extends MongoRepository<User, String> {
    boolean existsByEmail(String email);
    boolean existsById(String id);
    boolean existsByNameAndSurname(String name, String surname);

}
