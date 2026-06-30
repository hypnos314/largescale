package it.unipi.largescale.DiscoverEurope.service.mongodb;

import it.unipi.largescale.DiscoverEurope.DTO.mongodb.FullFlightSummaryDTO;
import it.unipi.largescale.DiscoverEurope.DTO.neo4j.CustomPoiDTO;
import it.unipi.largescale.DiscoverEurope.event.Task;
import it.unipi.largescale.DiscoverEurope.event.TaskToDo;
import it.unipi.largescale.DiscoverEurope.model.mongodb.Flight;
import it.unipi.largescale.DiscoverEurope.model.mongodb.TravelPackage;
import it.unipi.largescale.DiscoverEurope.model.mongodb.User;
import it.unipi.largescale.DiscoverEurope.model.mongodb.embeddedUser.*;
import it.unipi.largescale.DiscoverEurope.repository.mongodb.Flight_MongoInterface;
import it.unipi.largescale.DiscoverEurope.repository.mongodb.TravelPackage_MongoInterface;
import it.unipi.largescale.DiscoverEurope.repository.mongodb.User_MongoInterface;
import it.unipi.largescale.DiscoverEurope.service.Neo4jSyncManager;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

/**
 * Service handling shopping cart management, order checkout, and purchase history logic.
 */
@Service
public class CartAndOrder_Service_Mongo {

    @Autowired
    private User_MongoInterface userMongoInterface;
    @Autowired
    private TravelPackage_MongoInterface travelPackageRepository;
    @Autowired
    private Flight_MongoInterface flightRepository;
    @Autowired
    private Neo4jSyncManager syncManager;

    /**
     * Retrieves a user's shopping cart, applying frontend censorship to hide
     * the actual destination of "Surprise" packages.
     * @param userId the ID of the user
     * @return the current {@link Cart}
     * @throws RuntimeException if the user is not found
     */
    public Cart getUserCart(String userId) {
        User user = userMongoInterface.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Cart cart = user.getCart();
        if (cart == null)
            return new Cart();

        if (cart.getItems() != null) {
            for (Item item : cart.getItems()) {
                if (item.isSurprise()) {
                    item.setName(item.getCensoredTitle());
                    item.setDestination("Secret Destination");
                    if (item.getFlightSummary() != null) {
                        if (item.getFlightSummary().getOutboundFlight() != null) {
                            item.getFlightSummary().getOutboundFlight().setArrivalCity("Secret Destination");
                        }
                        if (item.getFlightSummary().getReturnFlight() != null) {
                            item.getFlightSummary().getReturnFlight().setDepartureCity("Secret Destination");
                        }
                    }
                } else {
                    item.setCensoredTitle(null);
                    item.setClues(null);
                }
            }
        }
        return cart;
    }

    /**
     * Adds a travel package and specific flight selections to the user's cart.
     * @param userId the ID of the user
     * @param packageId the ID of the selected travel package
     * @param outboundFlightId the ID of the chosen outbound flight
     * @param returnFlightId the ID of the chosen return flight
     * @param isSurprise boolean flag indicating if it's a mystery trip
     * @return a success confirmation message
     * @throws RuntimeException if user or package are not found
     */
    public String addPackageToCart(String userId, String packageId, String outboundFlightId, String returnFlightId, boolean isSurprise) {
        User user = userMongoInterface.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        TravelPackage pkg = travelPackageRepository.findById(packageId)
                .orElseThrow(() -> new RuntimeException("Travel package not found"));

        Flight outboundFlight = (outboundFlightId != null) ?
                flightRepository.findById(outboundFlightId).orElse(null) : null;
        Flight returnFlight = (returnFlightId != null) ?
                flightRepository.findById(returnFlightId).orElse(null) : null;

        Cart cart = user.getCart();
        if (cart == null) {
            cart = new Cart();
            cart.setItems(new ArrayList<>());
            cart.setTotalEstimated(0.0);
        }

        double finalPrice = pkg.getPrice();

        Item newItem = new Item();
        newItem.setPackageId(new ObjectId(pkg.getId()));
        newItem.setName(pkg.getTitle());
        newItem.setDestination(pkg.getCity());
        newItem.setSurprise(isSurprise);
        newItem.setAddedAt(Instant.now());

        if (outboundFlight != null && returnFlight != null) {
            EmbeddedFlight embOut = new EmbeddedFlight();
            embOut.setFlightId(outboundFlight.getId());
            embOut.setDepartureCity(outboundFlight.getDeparture().getCity());
            embOut.setArrivalCity(outboundFlight.getArrival().getCity());
            embOut.setDepartureDate(outboundFlight.getDeparture().getAt());
            embOut.setArrivalDate(outboundFlight.getArrival().getAt());

            EmbeddedFlight embRet = new EmbeddedFlight();
            embRet.setFlightId(returnFlight.getId());
            embRet.setDepartureCity(returnFlight.getDeparture().getCity());
            embRet.setArrivalCity(returnFlight.getArrival().getCity());
            embRet.setDepartureDate(returnFlight.getDeparture().getAt());
            embRet.setArrivalDate(returnFlight.getArrival().getAt());

            EmbeddedFlightSummary summary = new EmbeddedFlightSummary();
            summary.setOutboundFlight(embOut);
            summary.setReturnFlight(embRet);

            newItem.setFlightSummary(summary);
            finalPrice += (outboundFlight.getPrice() + returnFlight.getPrice());
        }

        finalPrice = Math.round(finalPrice * 100.0) / 100.0;
        newItem.setPrice(finalPrice);

        if (isSurprise) {
            newItem.setCensoredTitle("Mystery Trip: " + pkg.getCity().length() + " letters destination");
            if (pkg.getClues() != null) {
                newItem.setClues(new ArrayList<>(pkg.getClues()));
            } else {
                newItem.setClues(List.of("A beautiful secret place in Europe!"));
            }
        }

        cart.getItems().add(newItem);
        double newTotal = cart.getTotalEstimated() + finalPrice;
        cart.setTotalEstimated(Math.round(newTotal * 100.0) / 100.0);
        user.setCart(cart);
        userMongoInterface.save(user);
        return "Package added to cart successfully";
    }

    /**
     * Removes a specific package from the cart and records it as a cancelled order in the user's history.
     * @param userId the ID of the user
     * @param packageId the ID of the package to remove
     * @return a success confirmation message
     * @throws RuntimeException if the user, cart, or package are not found
     */
    public String removePackageFromCart(String userId, String packageId) {
        User user = userMongoInterface.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        Cart cart = user.getCart();

        if (cart == null || cart.getItems() == null || cart.getItems().isEmpty()) {
            throw new RuntimeException("Cart is empty or not found");
        }

        Item itemToRemove = null;
        for (Item item : cart.getItems()) {
            if (item.getPackageId().equals(packageId)) {
                itemToRemove = item;
                break;
            }
        }
        if (itemToRemove == null) throw new RuntimeException("Package not found in cart");

        cart.getItems().remove(itemToRemove);
        double newTotal = cart.getTotalEstimated() - itemToRemove.getPrice();
        cart.setTotalEstimated(Math.max(0.0, newTotal));

        List<Order> history = user.getOrders();
        if (history == null) history = new ArrayList<>();

        Order cancelledOrder = new Order();
        cancelledOrder.setOrderId(new ObjectId());
        cancelledOrder.setPackageId(itemToRemove.getPackageId());
        cancelledOrder.setPrice(itemToRemove.getPrice());
        cancelledOrder.setSurprise(itemToRemove.isSurprise());
        cancelledOrder.setPurchaseDate(Instant.now());
        cancelledOrder.setOrderStatus("cancelled");

        cancelledOrder.setName(itemToRemove.getName());
        cancelledOrder.setDestination(itemToRemove.getDestination());
        cancelledOrder.setCensoredTitle(itemToRemove.getCensoredTitle());
        cancelledOrder.setClues(itemToRemove.getClues());
        cancelledOrder.setFlightSummary(itemToRemove.getFlightSummary());

        history.add(cancelledOrder);

        user.setCart(cart);
        user.setOrders(history);
        userMongoInterface.save(user);

        return "Package removed from cart and marked as CANCELLED successfully";
    }


    /**
     * Processes the checkout, converting all cart items into confirmed orders
     * and queuing synchronization tasks for Neo4j.
     * @param userId the ID of the user
     * @return a success confirmation message
     * @throws RuntimeException if the user is not found or cart is empty
     */
    public String checkoutCart(String userId) {
        User user = userMongoInterface.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Cart cart = user.getCart();
        if (cart == null || cart.getItems() == null || cart.getItems().isEmpty()) {
            throw new RuntimeException("Cart is empty");
        }

        List<Order> history = user.getOrders();
        if (history == null) history = new ArrayList<>();

        List<TaskToDo> pendingSyncTasks = new ArrayList<>();

        for (Item item : cart.getItems()) {
            Order newOrder = new Order();
            newOrder.setOrderId(new ObjectId());
            newOrder.setPackageId(item.getPackageId());
            newOrder.setPrice(item.getPrice());
            newOrder.setSurprise(item.isSurprise());
            newOrder.setPurchaseDate(Instant.now().truncatedTo(ChronoUnit.SECONDS));
            newOrder.setOrderStatus("confirmed");

            newOrder.setName(item.getName());
            newOrder.setDestination(item.getDestination());
            newOrder.setCensoredTitle(item.getCensoredTitle());
            newOrder.setClues(item.getClues());
            newOrder.setFlightSummary(item.getFlightSummary());

            history.add(newOrder);

            pendingSyncTasks.add(new TaskToDo(
                    Task.TaskType.ADD_PURCHASED_RELATION, userId, item.getPackageId().toString(),
                    newOrder.getPurchaseDate().toString()
            ));
        }

        user.getCart().getItems().clear();
        user.getCart().setTotalEstimated(0.0);
        user.setOrders(history);
        userMongoInterface.save(user);

        for (TaskToDo task : pendingSyncTasks) {
            syncManager.addTask(task);
        }
        return "Checkout completed successfully";
    }

    /**
     * Appends a custom Point of Interest to an existing confirmed order.
     * @param userId the ID of the user
     * @param orderId the ID of the order
     * @param poiDto the custom POI details
     */
    public void addPoiToOrder(String userId, String orderId, CustomPoiDTO poiDto) {
        ObjectId userObjectId = new ObjectId(userId);
        ObjectId orderObjectId = new ObjectId(orderId);
        userMongoInterface.addNeo4jPoiToOrder(userObjectId, orderObjectId, poiDto);
    }

    /**
     * Retrieves the complete order history of a user. Automatically reveals the true destination
     * of surprise packages only if the return date has passed.
     * @param userId the ID of the user
     * @return a list of historical {@link Order}
     * @throws RuntimeException if the user is not found
     */
    public List<Order> getUserHistory(String userId) {
        User user = userMongoInterface.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        List<Order> history = user.getOrders();
        if (history == null) return new ArrayList<>();

        Instant now = Instant.now();

        for (Order order : history) {
            if (order.isSurprise()) {
                if (order.getFlightSummary() != null && order.getFlightSummary().getReturnFlight() != null) {
                    Instant returnDate = order.getFlightSummary().getReturnFlight().getArrivalDate();
                    if (returnDate == null || !now.isAfter(returnDate)) {
                        order.setName(order.getCensoredTitle());
                        order.setDestination("Secret Destination");

                        if (order.getFlightSummary() != null) {
                            if (order.getFlightSummary().getOutboundFlight() != null) {
                                order.getFlightSummary().getOutboundFlight().setArrivalCity("Secret Destination");
                            }
                            if (order.getFlightSummary().getReturnFlight() != null) {
                                order.getFlightSummary().getReturnFlight().setDepartureCity("Secret Destination");
                            }
                        }
                    }
                } else {
                    order.setName(order.getCensoredTitle());
                    order.setDestination("Secret Destination");
                }
            }
        }
        return history;
    }

    /**
     * Retrieves detailed flight information for a specific order, dynamically applying
     * a "Surprise Protection" filter to mask the destination if the trip has not yet concluded.
     * Automatically strips internal MongoDB ObjectIDs from the response to ensure clean JSON serialization.
     * @param userId the ID of the user owning the order
     * @param orderId the ID of the specific order
     * @return a {@link FullFlightSummaryDTO} containing the flights, or null if no flights exist
     * @throws RuntimeException if the user or order cannot be found in the database
     */
    public FullFlightSummaryDTO getOrderFlightDetails(String userId, String orderId) {
        User user = userMongoInterface.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        if (user.getOrders() == null) {
            throw new RuntimeException("No orders found for this user");
        }
        Order targetOrder = user.getOrders().stream()
                .filter(o -> o.getOrderId().toString().equals(orderId))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Order not found"));

        EmbeddedFlightSummary embeddedFlights = targetOrder.getFlightSummary();
        if (embeddedFlights == null)
            return null;

        FullFlightSummaryDTO response = new FullFlightSummaryDTO();

        Flight outbound = null;
        if (embeddedFlights.getOutboundFlight() != null && embeddedFlights.getOutboundFlight().getFlightId() != null) {
            outbound = flightRepository.findById(embeddedFlights.getOutboundFlight().getFlightId().toString()).orElse(null);
        }

        Flight returnFlight = null;
        if (embeddedFlights.getReturnFlight() != null && embeddedFlights.getReturnFlight().getFlightId() != null) {
            returnFlight = flightRepository.findById(embeddedFlights.getReturnFlight().getFlightId().toString()).orElse(null);
        }

        if (targetOrder.isSurprise() && embeddedFlights.getReturnFlight() != null) {
            Instant returnDate = embeddedFlights.getReturnFlight().getArrivalDate();
            Instant now = Instant.now();

            if (returnDate == null || !now.isAfter(returnDate)) {
                if (outbound != null) {
                    outbound.getArrival().setCity("Secret Destination");
                    outbound.getArrival().setAirportIata("???");
                }
                if (returnFlight != null) {
                    returnFlight.getDeparture().setCity("Secret Destination");
                    returnFlight.getDeparture().setAirportIata("???");
                }
            }
        }
        if (outbound != null)
            outbound.setId(null);
        if (returnFlight != null)
            returnFlight.setId(null);

        response.setOutboundFlight(outbound);
        response.setReturnFlight(returnFlight);

        return response;
    }
}