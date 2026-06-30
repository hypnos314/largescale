package it.unipi.largescale.DiscoverEurope.controller.mongodb;

import it.unipi.largescale.DiscoverEurope.DTO.mongodb.*;
import it.unipi.largescale.DiscoverEurope.model.mongodb.User;
import it.unipi.largescale.DiscoverEurope.service.mongodb.User_Service_Mongo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * User's REST Controller.
 */
@RestController
@RequestMapping("/api/users")
public class User_Controller_Mongo {
    @Autowired
    private User_Service_Mongo userServiceMongo;

    /**
     * Retrieves the complete profile of a user.
     * @param userId the unique identifier of the user.
     * @return a {@link ResponseEntity} containing the user data.
     */
    @GetMapping("/{userId}")
    public ResponseEntity<UserDTO> getUserProfile(@PathVariable String userId) {
        try {
            // Una sola riga pulita: il service fa tutto e restituisce già l'UserDTO!
            return ResponseEntity.ok(userServiceMongo.getUserAccount(userId));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    /**
     * Registers a new user account in the system.
     * @param request data containing credentials, personal info, and identity documents.
     * @return a {@link ResponseEntity} with status 201 if created or 400 if validation fails.
     */
    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody RegistrationDTO request){
        try{
            String result = userServiceMongo.registerUser(
                    request.getCredentials(),
                    request.getPersonalInfo(),
                    request.getIdentityDocuments()
            );
            if(result.equals("User saved")){
                return ResponseEntity.status(HttpStatus.CREATED).body(result);
            }else{
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(result);
            }
        }catch(Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Internal server error");
        }
    }

    /**
     * Authenticates a user and returns the user object upon success.
     * @param loginData login credentials (email and password).
     * @return a {@link ResponseEntity} with the user profile or 401 if unauthorized.
     */
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginDTO loginData) {
        User user = userServiceMongo.loginUser(loginData.getEmail(), loginData.getPassword());
        if (user != null) {
            LoginResponseDTO response = new LoginResponseDTO("Login successful", user.getId());
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Credentials not valid");
        }
    }

    /**
     * Updates user identity documents and password settings.
     * @param userId the unique identifier of the user.
     * @param request object containing new documents or password.
     */
    @PutMapping("/{userId}/settings")
    public ResponseEntity<String> updateSettings(
            @PathVariable String userId,
            @RequestBody UpdateProfileDTO request
            ){
        try{
            String result = userServiceMongo.updateAccountSettings(userId, request.getIdentityDocuments(), request.getNewPassword());
            if (result.equals("Account settings updated successfully")) {
                return ResponseEntity.ok(result);
            } else if (result.equals("User not found")) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(result);
            } else {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(result);
            }
        }catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Internal server error");
        }
    }

    /**
     * Deletes the user account permanently.
     * @param userId the unique identifier of the user.
     */
    @DeleteMapping("/{userId}")
    public ResponseEntity<String> deleteAccount(@PathVariable String userId) {
        String result = userServiceMongo.deleteUserAccount(userId);
        if (result.equals("Account deleted successfully")) {
            return ResponseEntity.ok(result);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(result);
        }
    }
}
