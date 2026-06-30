package it.unipi.largescale.DiscoverEurope.controller.mongodb;

import it.unipi.largescale.DiscoverEurope.DTO.mongodb.AddToCartDTO;
import it.unipi.largescale.DiscoverEurope.DTO.mongodb.FullFlightSummaryDTO;
import it.unipi.largescale.DiscoverEurope.DTO.neo4j.CustomPoiDTO;
import it.unipi.largescale.DiscoverEurope.model.mongodb.embeddedUser.Cart;
import it.unipi.largescale.DiscoverEurope.model.mongodb.embeddedUser.Order;
import it.unipi.largescale.DiscoverEurope.service.mongodb.CartAndOrder_Service_Mongo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST Controller for cart and orders of the user.
 */
@RestController
@RequestMapping("/api/users/{userId}")
public class CartAndOrder_Controller_Mongo {
    @Autowired
    private CartAndOrder_Service_Mongo cartAndOrderService;

    /**
     * Retrieves the shopping cart for a specific user.
     * @param userId the unique identifier of the user.
     * @return a {@link ResponseEntity} containing the user's {@link Cart}.
     */
    @GetMapping("/cart")
    public ResponseEntity<?> getUserCart(@PathVariable String userId) {
        try {
            Cart cart = cartAndOrderService.getUserCart(userId);
            return ResponseEntity.ok(cart);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Internal server error");
        }
    }

    /**
     * Adds a travel package to the user's shopping cart.
     * @param userId the user identifier.
     * @param request the package details including flights and surprise status.
     * @return a {@link ResponseEntity} with a success message.
     */
    @PostMapping("/cart/items")
    public ResponseEntity<String> addPackageToCart(
            @PathVariable String userId,
            @RequestBody AddToCartDTO request) {
        try {
            //modificato
            String result = cartAndOrderService.addPackageToCart(
                    userId,
                    request.getPackageId(),
                    request.getOutboundFlightId(),
                    request.getReturnFlightId(),
                    request.isSurprise()
            );                return ResponseEntity.ok(result);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error adding item to cart");
        }
    }

    /**
     * Removes a specific travel package from the user's shopping cart.
     * @param userId the user identifier.
     * @param packageId the package identifier to remove.
     */
    @DeleteMapping("/remove/{packageId}")
    public ResponseEntity<String> removePackageFromCart(@PathVariable String userId, @PathVariable String packageId) {
        try {
            String result = cartAndOrderService.removePackageFromCart(userId, packageId);
            return ResponseEntity.ok(result);

        } catch (RuntimeException e) {

            if (e.getMessage().equals("User not found") || e.getMessage().contains("Package not found in cart")) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
            }
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error while removing package");
        }
    }

    /**
     * Adds a custom Point of Interest to an existing order.
     * @param userId the user identifier.
     * @param orderId the order identifier.
     * @param poiDto the custom POI details.
     */
    @PostMapping("/orders/{orderId}/add-poi")
    public ResponseEntity<String> addPoiToOrder(
            @PathVariable String userId,
            @PathVariable String orderId,
            @RequestBody CustomPoiDTO poiDto) {
        try {
            cartAndOrderService.addPoiToOrder(userId, orderId, poiDto);
            return ResponseEntity.ok("Successfully added POI to order .");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error: " + e.getMessage());
        }
    }


    /**
     * Processes the checkout of the shopping cart, creating a new order.
     * @return a {@link ResponseEntity} confirming the order creation.
     */
    @PostMapping("/cart/checkout")
    public ResponseEntity<String> checkoutCart(@PathVariable String userId) {
        try {
            String result = cartAndOrderService.checkoutCart(userId);
            return ResponseEntity.ok(result);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error processing checkout");
        }
    }

    /**
     * Retrieves the history of all completed orders for a user.
     * @return a {@link ResponseEntity} containing a list of {@link Order}.
     */
    @GetMapping("/orders")
    public ResponseEntity<?> getUserHistory(@PathVariable("userId") String userId) {
        try {
            List<Order> history = cartAndOrderService.getUserHistory(userId);
            return ResponseEntity.ok(history);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error retrieving order history");
        }
    }

    /**
     * Retrieves the complete flight itinerary details for a specific confirmed order.
     * Automatically handles data censorship for active surprise packages to prevent spoilers.
     * @param userId the unique identifier of the user.
     * @param orderId the unique identifier of the order.
     * @return a {@link ResponseEntity} containing the {@link FullFlightSummaryDTO}.
     */
    @GetMapping("/orders/{orderId}/flights")
    public ResponseEntity<FullFlightSummaryDTO> getOrderFlightDetails(
            @PathVariable String userId,
            @PathVariable String orderId) {
        try {
            FullFlightSummaryDTO flightDetails = cartAndOrderService.getOrderFlightDetails(userId, orderId);
            if (flightDetails == null) {
                return ResponseEntity.noContent().build();
            }
            return ResponseEntity.ok(flightDetails);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(null);
        }
    }
}
