package it.unipi.largescale.DiscoverEurope.controller;

import it.unipi.largescale.DiscoverEurope.DTO.AddToCartDTO;
import it.unipi.largescale.DiscoverEurope.model.embeddedUser.Cart;
import it.unipi.largescale.DiscoverEurope.model.embeddedUser.Order;
import it.unipi.largescale.DiscoverEurope.service.CartAndOrder_Service_Mongo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users/{userId}")
public class CartAndOrder_Controller_Mongo {
        @Autowired
        private CartAndOrder_Service_Mongo cartAndOrderService;

        // ==========================================
        // 1. VISUALIZZA IL CARRELLO
        // ==========================================
        @GetMapping("/cart")
        public ResponseEntity<?> getUserCart(@PathVariable String userId) {
            try {
                Cart cart = cartAndOrderService.getUserCart(userId);
                return ResponseEntity.ok(cart); // 200 OK
            } catch (RuntimeException e) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage()); // 404
            } catch (Exception e) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Internal server error"); // 500
            }
        }

        // ==========================================
        // 2. AGGIUNGI AL CARRELLO
        // ==========================================
        @PostMapping("/cart/items")
        public ResponseEntity<String> addPackageToCart(
                @PathVariable String userId,
                @RequestBody AddToCartDTO request) {
            try {
                String result = cartAndOrderService.addPackageToCart(userId, request.getPackageId(), request.isSurprise());
                return ResponseEntity.ok(result); // 200 OK
            } catch (RuntimeException e) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage()); // 400 (es. pacchetto non trovato)
            } catch (Exception e) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error adding item to cart"); // 500
            }
        }

        // ==========================================
        // 3. CHECKOUT
        // ==========================================
        @PostMapping("/cart/checkout")
        public ResponseEntity<String> checkoutCart(@PathVariable String userId) {
            try {
                String result = cartAndOrderService.checkoutCart(userId);
                return ResponseEntity.ok(result); // 200 OK
            } catch (RuntimeException e) {
                // Se il carrello è vuoto, restituisce 400 Bad Request
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
            } catch (Exception e) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error processing checkout"); // 500
            }
        }

        // ==========================================
        // 4. STORICO ORDINI
        // ==========================================
        @GetMapping("/orders")
        public ResponseEntity<?> getUserHistory(@PathVariable String userId) {
            try {
                List<Order> history = cartAndOrderService.getUserHistory(userId);
                return ResponseEntity.ok(history); // 200 OK
            } catch (RuntimeException e) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage()); // 404
            } catch (Exception e) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error retrieving order history"); // 500
            }
        }

}
