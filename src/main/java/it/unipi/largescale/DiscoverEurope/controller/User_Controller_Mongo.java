package it.unipi.largescale.DiscoverEurope.controller;

import it.unipi.largescale.DiscoverEurope.DTO.LoginDTO;
import it.unipi.largescale.DiscoverEurope.DTO.RegistrationDTO;
import it.unipi.largescale.DiscoverEurope.DTO.UpdateProfileDTO;
import it.unipi.largescale.DiscoverEurope.model.User;
import it.unipi.largescale.DiscoverEurope.service.User_Service_Mongo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
public class User_Controller_Mongo {
    @Autowired
    private User_Service_Mongo userServiceMongo;

    @GetMapping("/{userId}")
    public ResponseEntity<?> getUserProfile(@PathVariable String userId) {
        try {
            User user = userServiceMongo.getUserAccount(userId);

            return ResponseEntity.ok(user); // restituisce i dati dell'utente in formato JSON
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
        }
    }

    /*

    // ==========================================
    // RECUPERA LO STORICO VIAGGI (ORDINI)
    // ==========================================
    @GetMapping("/{userId}/history")
    public ResponseEntity<List<Order>> getUserHistory(@PathVariable String userId) {
        try {
            // 1. Cerchiamo l'utente nel database
            Optional<User> userOpt = user_Interf_Mongo.findById(userId);

            if (userOpt.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).build(); // 404
            }

            // 2. Estraiamo la sua lista di ordini
            List<Order> history = userOpt.get().getOrders();

            // 3. Se la lista è null (nessun ordine mai fatto), restituiamo una lista vuota
            if (history == null) {
                return ResponseEntity.ok(new ArrayList<>());
            }

            // 4. Restituiamo lo storico al frontend
            return ResponseEntity.ok(history); // 200 OK

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build(); // 500
        }
    }
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
                return ResponseEntity.status(HttpStatus.CREATED).body(result); //201 created

            }else{
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(result); //400 bad request
            }
        }catch(Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Internal server error");

        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginDTO loginData){
        User user = userServiceMongo.loginUser(loginData.getEmail(), loginData.getPassword());

        if(user != null){
            return ResponseEntity.ok(user); //200 ok

        }else{
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Credentials not valid"); //401 unauthorized
        }
    }

    @PutMapping("/{userId}/settings")
    public ResponseEntity<String> updateSettings(
            @PathVariable String userId, //uso pathvariable che prende lo userid direttamente dall'url
            @RequestBody UpdateProfileDTO request
            ){
        try{
            String result = userServiceMongo.updateAccountSettings(userId, request.getIdentityDocuments(), request.getNewPassword());
            if (result.equals("Account settings updated successfully")) {
                return ResponseEntity.ok(result);
            } else if (result.equals("User not found")) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(result); // 404 Not Found
            } else {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(result);
            }
        }catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Errore interno del server");
        }
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<String> deleteAccount(@PathVariable String userId) {
        String result = userServiceMongo.deleteUserAccount(userId);

        if (result.equals("Account deleted successfully")) {
            return ResponseEntity.ok(result); // HTTP 200
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(result); // HTTP 404
        }
    }

}
