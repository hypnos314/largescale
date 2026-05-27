package it.unipi.largescale.DiscoverEurope.service;

import com.google.common.hash.Hashing;
import it.unipi.largescale.DiscoverEurope.model.User;
import it.unipi.largescale.DiscoverEurope.model.embeddedUser.*;
import it.unipi.largescale.DiscoverEurope.repository.User_MongoInterface;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class User_Service_Mongo {
    @Autowired
    private User_MongoInterface user_Interf_Mongo;

    public String registerUser(Credentials credentials, PersonalInfo personalInfo, List<IdentityDocument> identityDocuments){
        try{
            //validazione credentials
            if (credentials == null || credentials.getEmail() == null || credentials.getEmail().trim().isEmpty()
                    || credentials.getPassword() == null || credentials.getPassword().trim().isEmpty()) {
                return "Missing or invalid credentials (email and password are required)";
            }

            //validazione personalInfo
            if (personalInfo == null
                    || personalInfo.getFirstName() == null || personalInfo.getFirstName().trim().isEmpty()
                    || personalInfo.getLastName() == null || personalInfo.getLastName().trim().isEmpty()
                    || personalInfo.getAge() == null // c'è da recuperare l'età dalla birth date
                    || personalInfo.getPhone() == null || personalInfo.getPhone().trim().isEmpty()) {

                return "All personal info fields (first name, last name, birth date, phone) are required";
            }

            //validazione documents
            List<IdentityDocument> verifiedDocuments;

            if (identityDocuments == null || identityDocuments.isEmpty()) {
                // se l'utente non li inserisce ora, inizializziamo una lista vuota per evitare futuri NullPointerException
                verifiedDocuments = new ArrayList<>();
            } else {
                // se invece ha provato a inserirli, facciamo comunque un controllo di validità sui campi minimi
                for (IdentityDocument doc : identityDocuments) {
                    if (doc.getType() == null || doc.getNumber() == null || doc.getNumber().trim().isEmpty() || doc.getExpiryDate() == null) {
                        return "Invalid identity document details provided";
                    }
                    if(doc.getExpiryDate().isBefore(LocalDate.now())){
                        return "The document you inserted is expired";
                    }
                }
                verifiedDocuments = identityDocuments;
            }

            if(user_Interf_Mongo.existsByCredentialsEmail(credentials.getEmail())){
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

            User newUser = new User(null, "customer", credentials, personalInfo, verifiedDocuments, new Cart(), orders);
            user_Interf_Mongo.save(newUser);
            return "User saved";

        } catch (Exception e) {
            throw new RuntimeException("Error during user registration",e);
        }
    }

    public User loginUser(String email, String password){

        Optional<User> userFound = user_Interf_Mongo.findByCredentialsEmail(email);

        if (userFound.isEmpty()) {
            System.out.println("Failed login: email not found");
            return null;
        }

        User user = userFound.get();

        //Hashing della password
        String hashedInputPassword = Hashing.sha256()
                .hashString(password, StandardCharsets.UTF_8)
                .toString();

        if (user.getCredentials().getPassword().equals(hashedInputPassword)) {
            System.out.println("Valid credentials for user: " + email);
            return user;
        }

        System.out.println("Failed login: wrong password");
        return null;
    }

    //Manage account per aggiungere documenti
    public String updateAccountSettings(String userId, List<IdentityDocument> newDocs, String newPassword) {
        try {
            Optional<User> userOptional = user_Interf_Mongo.findById(userId);
            if (userOptional.isEmpty()) {
                return "User not found";
            }
            User user = userOptional.get();

            boolean isUpdated = false;

            //aggiornamento password
            if (newPassword != null && !newPassword.trim().isEmpty()) {
                String hashedNewPassword = Hashing.sha256()
                        .hashString(newPassword, StandardCharsets.UTF_8)
                        .toString();

                user.getCredentials().setPassword(hashedNewPassword);
                isUpdated = true;
                System.out.println("Password updated for user: " + userId);
            }

            //aggiornamento documenti
            if (newDocs != null && !newDocs.isEmpty()) {
                LocalDate now = LocalDate.now();

                // validazione dei documenti in arrivo nel payload
                for (IdentityDocument doc : newDocs) {
                    if (doc.getType() == null || doc.getNumber() == null
                            || doc.getNumber().trim().isEmpty() || doc.getExpiryDate() == null) {
                        return "Insert missing information in documents";
                    }
                    if (doc.getExpiryDate().isBefore(now)) {
                        return "The document you inserted is expired";
                    }
                }
                user.setIdentityDocuments(newDocs);

                isUpdated = true;
                System.out.println("Identity documents overwritten for user: " + userId);
            }

            if (isUpdated) {
                user_Interf_Mongo.save(user);
                return "Account settings updated successfully";
            } else {
                return "No changes detected";
            }

        } catch (Exception e) {
            throw new RuntimeException("Error while updating account settings", e);
        }
    }

    // Eliminazione Account
    public String deleteUserAccount(String userId) {
        try {
            if (!user_Interf_Mongo.existsById(userId)) {
                return "User not found";
            }
            user_Interf_Mongo.deleteById(userId);
            return "Account deleted successfully";
        } catch (Exception e) {
            throw new RuntimeException("Error during account deletion", e);
        }
    }

    // Recupero Profilo (da mappare poi nel DTO nel Controller)
    public User getUserAccount(String userId) {
        return user_Interf_Mongo.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

}
