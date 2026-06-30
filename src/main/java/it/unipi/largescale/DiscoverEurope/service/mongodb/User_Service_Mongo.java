package it.unipi.largescale.DiscoverEurope.service.mongodb;

import com.google.common.hash.Hashing;
import it.unipi.largescale.DiscoverEurope.DTO.mongodb.UserDTO;
import it.unipi.largescale.DiscoverEurope.event.Task;
import it.unipi.largescale.DiscoverEurope.event.TaskToDo;
import it.unipi.largescale.DiscoverEurope.model.mongodb.User;
import it.unipi.largescale.DiscoverEurope.model.mongodb.embeddedUser.*;
import it.unipi.largescale.DiscoverEurope.repository.mongodb.User_MongoInterface;
import it.unipi.largescale.DiscoverEurope.service.Neo4jSyncManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Service handling user identity, authentication, and account management operations.
 */
@Service
public class User_Service_Mongo {

    @Autowired
    private User_MongoInterface user_Interf_Mongo;
    @Autowired
    private Neo4jSyncManager syncManager;

    /**
     * Registers a new user in the system, hashes their password, and initiates graph synchronization.
     * @param credentials the user's login details
     * @param personalInfo the user's demographic data
     * @param identityDocuments list of user's identity documents
     * @return a status message indicating success or validation failure
     * @throws RuntimeException if an unexpected error occurs during registration
     */
    public String registerUser(Credentials credentials, PersonalInfo personalInfo, List<IdentityDocument> identityDocuments){
        try{
            if (credentials == null || credentials.getEmail() == null || credentials.getEmail().trim().isEmpty()
                    || credentials.getPassword() == null || credentials.getPassword().trim().isEmpty()) {
                return "Missing or invalid credentials (email and password are required)";
            }
            if (personalInfo == null
                    || personalInfo.getFirstName() == null || personalInfo.getFirstName().trim().isEmpty()
                    || personalInfo.getLastName() == null || personalInfo.getLastName().trim().isEmpty()
                    || personalInfo.getAge() == null
                    || personalInfo.getPhone() == null || personalInfo.getPhone().trim().isEmpty()) {
                return "All personal info fields (first name, last name, birth date, phone) are required";
            }
            List<IdentityDocument> verifiedDocuments;
            if (identityDocuments == null || identityDocuments.isEmpty()) {
                verifiedDocuments = new ArrayList<>();
            } else {
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
            if(user_Interf_Mongo.existsByPersonalInfoFirstNameAndPersonalInfoLastName(personalInfo.getFirstName(), personalInfo.getLastName())){
                return "Name and Surname already exist";
            }
            String hashedPassword = Hashing.sha256()
                    .hashString(credentials.getPassword(), StandardCharsets.UTF_8)
                    .toString();

            credentials.setPassword(hashedPassword);

            List<Order> orders = new ArrayList<>();

            User newUser = new User(null, "customer", credentials, personalInfo, verifiedDocuments, new Cart(), orders);
            user_Interf_Mongo.save(newUser);

            TaskToDo syncTask = new TaskToDo(Task.TaskType.SYNC_NEW_USER, newUser);
            syncManager.addTask(syncTask);
            return "User saved";
        } catch (Exception e) {
            throw new RuntimeException("Error during user registration",e);
        }
    }

    /**
     * Authenticates a user by validating their email and hashed password.
     * @param email the login email
     * @param password the plain-text password to verify
     * @return the authenticated {@link User} entity, or null if validation fails
     */
    public User loginUser(String email, String password){
        Optional<User> userFound = user_Interf_Mongo.findByCredentialsEmail(email);
        if (userFound.isEmpty()) {
            System.out.println("Failed login: email not found");
            return null;
        }
        User user = userFound.get();

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

    /**
     * Updates sensitive user account settings such as passwords and identity documents.
     * @param userId the ID of the user
     * @param newDocs the updated list of identity documents
     * @param newPassword the new plain-text password to be hashed
     * @return a status message indicating the result
     * @throws RuntimeException if user is not found or database error occurs
     */
    public String updateAccountSettings(String userId, List<IdentityDocument> newDocs, String newPassword) {
        try {
            Optional<User> userOptional = user_Interf_Mongo.findById(userId);
            if (userOptional.isEmpty()) {
                return "User not found";
            }
            User user = userOptional.get();

            boolean isUpdated = false;

            if (newPassword != null && !newPassword.trim().isEmpty()) {
                String hashedNewPassword = Hashing.sha256()
                        .hashString(newPassword, StandardCharsets.UTF_8)
                        .toString();

                user.getCredentials().setPassword(hashedNewPassword);
                isUpdated = true;
                System.out.println("Password updated for user: " + userId);
            }

            if (newDocs != null && !newDocs.isEmpty()) {
                LocalDate now = LocalDate.now();
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

    /**
     * Permanently deletes a user account from MongoDB and queues a deletion event for Neo4j.
     * @param userId the ID of the user to delete
     * @return a success confirmation message
     * @throws RuntimeException if user is not found or deletion fails
     */
    public String deleteUserAccount(String userId) {
        try {
            if (!user_Interf_Mongo.existsById(userId)) {
                return "User not found";
            }
            user_Interf_Mongo.deleteById(userId);

            TaskToDo syncTask = new TaskToDo(Task.TaskType.DELETE_USER_NEO4J, userId);
            syncManager.addTask(syncTask);
            return "Account deleted successfully";
        } catch (Exception e) {
            throw new RuntimeException("Error during account deletion", e);
        }
    }

    /**
     * Retrieves a sanitized user profile suitable for client transmission.
     * @param userId the ID of the user
     * @return a {@link UserDTO} containing safe profile information
     * @throws RuntimeException if the user is not found
     */
    public UserDTO getUserAccount(String userId) {
        User user = user_Interf_Mongo.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return UserDTO.fromEntity(user);
    }
}
