package it.unipi.largescale.DiscoverEurope.service;

import it.unipi.largescale.DiscoverEurope.model.TravelPackage;
import it.unipi.largescale.DiscoverEurope.model.User;
import it.unipi.largescale.DiscoverEurope.model.embeddedUser.Cart;
import it.unipi.largescale.DiscoverEurope.model.embeddedUser.Item;
import it.unipi.largescale.DiscoverEurope.model.embeddedUser.Order;
import it.unipi.largescale.DiscoverEurope.repository.TravelPackage_MongoInterface;
import it.unipi.largescale.DiscoverEurope.repository.User_MongoInterface;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class CartAndOrder_Service_Mongo {
    @Autowired
    private User_MongoInterface userMongoInterface;

    @Autowired
    private TravelPackage_MongoInterface travelPackageRepository;

    // ==========================================
    // 1. VISUALIZZA IL CARRELLO (Logica di censura)
    // ==========================================
    public Cart getUserCart(String userId) {
        User user = userMongoInterface.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Cart cart = user.getCart();
        if (cart == null) return new Cart();

        // SOVRASCRITTURA IN RAM PER IL FRONTEND
        if (cart.getItems() != null) {
            for (Item item : cart.getItems()) {
                if (item.isSurprise()) {
                    // Sostituisce temporaneamente il vero "name" con quello censurato
                    item.setName(item.getCensoredTitle());
                }
            }
        }
        return cart;
    }

    // ==========================================
    // 2. AGGIUNGI AL CARRELLO
    // ==========================================
    public String addPackageToCart(String userId, String packageId, boolean isSurprise) {
        User user = userMongoInterface.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        TravelPackage pkg = travelPackageRepository.findById(packageId)
                .orElseThrow(() -> new RuntimeException("Travel package not found"));

        Cart cart = user.getCart();
        if (cart == null) {
            cart = new Cart();
            cart.setItems(new ArrayList<>());
            cart.setTotalEstimated(0.0);
        }

        Item newItem = new Item();
        newItem.setPackageId(pkg.getId());
        newItem.setPrice(pkg.getPrice());
        newItem.setSurprise(isSurprise);
        newItem.setReturnDate(pkg.getFlightDetails().getReturnFlight().getArrival().getDate());

        // IL NOME REALE VA NEL CAMPO STANDARD
        newItem.setName(pkg.getTitle());

        // PREPARIAMO LA CENSURA E LA SALVIAMO A PARTE
        if (isSurprise && pkg.getCity() != null) {
            newItem.setCensoredTitle(pkg.getTitle().replace(pkg.getCity(), "a Mystery Destination"));
        } else {
            newItem.setCensoredTitle(pkg.getTitle());
        }

        cart.getItems().add(newItem);
        cart.setTotalEstimated(cart.getTotalEstimated() + pkg.getPrice());
        user.setCart(cart);

        userMongoInterface.save(user);

        return "Package added to cart successfully";
    }

    // ==========================================
    // 3. CHECKOUT (Crea ordine e svuota carrello)
    // ==========================================
    public String checkoutCart(String userId) {
        User user = userMongoInterface.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Cart cart = user.getCart();
        if (cart == null || cart.getItems() == null || cart.getItems().isEmpty()) {
            throw new RuntimeException("Cart is empty");
        }

        List<Order> history = user.getOrders();
        if (history == null) {
            history = new ArrayList<>();
        }

        for (Item item : cart.getItems()) {
            Order newOrder = new Order();
            //newOrder.setOrderId(UUID.randomUUID().toString());
            newOrder.setPackageId(item.getPackageId());
            newOrder.setPrice(item.getPrice());
            newOrder.setSurprise(item.isSurprise());
            newOrder.setPurchaseDate(Instant.now()); // Data di ACQUISTO
            newOrder.setOrderStatus("CONFIRMED");

            // TRAVASIAMO I DATI DAL CARRELLO ALL'ORDINE
            newOrder.setName(item.getName()); // Qui dentro c'è il VERO titolo
            newOrder.setCensoredTitle(item.getCensoredTitle());
            newOrder.setReturnDate(item.getReturnDate());

            history.add(newOrder);
        }

        user.getCart().getItems().clear();
        user.getCart().setTotalEstimated(0.0);
        user.setOrders(history);

        userMongoInterface.save(user);

        return "Checkout completed successfully";
    }

    // ==========================================
    // 4. STORICO ORDINI (La magia dello Svelamento)
    // ==========================================
    public List<Order> getUserHistory(String userId) {
        User user = userMongoInterface.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        List<Order> history = user.getOrders();
        if (history == null) return new ArrayList<>();

        Instant now = Instant.now();

        // LOGICA DI SVELAMENTO
        for (Order order : history) {
            if (order.isSurprise()) {
                // Se la data NON è ancora passata, nascondiamo il titolo vero
                if (order.getReturnDate() == null || !now.isAfter(order.getReturnDate())) {
                    order.setName(order.getCensoredTitle());
                }
                // (Se è passata, non facciamo nulla: il DB ha già il packageTitle originale intatto!)
            }
        }

        return history;
    }
}