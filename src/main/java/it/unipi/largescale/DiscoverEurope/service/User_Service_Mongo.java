package it.unipi.largescale.DiscoverEurope.service;

import com.google.common.hash.Hashing;
import it.unipi.largescale.DiscoverEurope.model.UserForRegistration;
import it.unipi.largescale.DiscoverEurope.model.embeddedUser.*;
import it.unipi.largescale.DiscoverEurope.repository.UserForRegistration_MongoInterface;
import it.unipi.largescale.DiscoverEurope.repository.User_MongoInterface;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

@Service
public class User_Service_Mongo {
    @Autowired
    private User_MongoInterface user_Interf_Mongo;
    @Autowired
    private UserForRegistration_MongoInterface userForRegistration_Interf_Mongo;

    public String registerUser(Credentials credentials, PersonalInfo personalInfo, List<IdentityDocument> identityDocuments){
        try{
            if(user_Interf_Mongo.existsByEmail(credentials.getEmail())){
                return "Email already exists";
            }
            if(user_Interf_Mongo.existsByNameAndSurname(personalInfo.getFirstName(), personalInfo.getLastName())){
                return "Name and Surname already exist";
            }

            // Hashing della password
            String hashedPassword = Hashing.sha256()
                    .hashString(credentials.getPassword(), StandardCharsets.UTF_8)
                    .toString();

            credentials.setPassword(hashedPassword);

            List<Order> orders = new ArrayList<>();

            UserForRegistration newUser = new UserForRegistration(null, credentials, personalInfo, identityDocuments, new Cart(), orders);
            userForRegistration_Interf_Mongo.save(newUser);
            return "User saved";

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
