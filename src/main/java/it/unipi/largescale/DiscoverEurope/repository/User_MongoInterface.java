package it.unipi.largescale.DiscoverEurope.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import it.unipi.largescale.DiscoverEurope.model.User;

import java.util.Optional;

public interface User_MongoInterface extends MongoRepository<User, String> {
    boolean existsByCredentialsEmail(String email);
    boolean existsById(String id);
    boolean existsByPersonalInfoFirstNameAndPersonalInfoLastName(String firstName, String lastName);
    Optional<User> findByCredentialsEmail(String email);
    Optional<User> findById(String id);
}
