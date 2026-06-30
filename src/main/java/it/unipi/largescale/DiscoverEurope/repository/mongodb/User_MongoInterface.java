package it.unipi.largescale.DiscoverEurope.repository.mongodb;

import it.unipi.largescale.DiscoverEurope.DTO.mongodb.MonthlyRevenueDTO;
import it.unipi.largescale.DiscoverEurope.DTO.mongodb.TopCustomerDTO;
import it.unipi.largescale.DiscoverEurope.DTO.neo4j.CustomPoiDTO;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.Aggregation;
import org.springframework.data.mongodb.repository.MongoRepository;
import it.unipi.largescale.DiscoverEurope.model.mongodb.User;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.mongodb.repository.Update;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository for User document management.
 * Handles authentication checks, order tracking, and high-level revenue analytics.
 */
@Repository
public interface User_MongoInterface extends MongoRepository<User, String> {
    boolean existsByCredentialsEmail(String email);
    boolean existsById(String id);
    boolean existsByPersonalInfoFirstNameAndPersonalInfoLastName(String firstName, String lastName);
    Optional<User> findByCredentialsEmail(String email);
    Optional<User> findById(String id);

    /**
     * Calculates total monthly revenue by aggregating the price field
     * across all confirmed orders in the system.
     * @return aggregated revenue metrics ordered chronologically
     */
    @Aggregation(pipeline = {
            "{ $match: { 'orders.order_status': 'confirmed' } }",
            "{ $unwind: '$orders' }",
            "{ $match: { 'orders.order_status': 'confirmed' } }",
            "{ $group: { _id: { year: { $year: '$orders.purchase_date' }, month: { $month: '$orders.purchase_date' } }, fatturato_mensile: { $sum: '$orders.price' }, numero_ordini: { $sum: 1 } } }",
            "{ $project: { _id: 0, Anno: '$_id.year', Mese: '$_id.month', Fatturato_Totale: { $round: ['$fatturato_mensile', 2] }, Numero_Ordini: '$numero_ordini' } }",
            "{ $sort: { Anno: -1, Mese: -1 } }"
    })
    List<MonthlyRevenueDTO> getMonthlyRevenue();

    /**
     * Identifies the platform's top customers based on the volume of confirmed orders.
     * Used for targeted marketing campaigns.
     * @return the top 10 customers
     */
    @Aggregation(pipeline = {
            "{ $match: { 'orders': { $exists: true, $ne: [] } } }",
            "{ $project: { _id: 1, first_name: '$personal_info.first_name', last_name: '$personal_info.last_name', email: '$credentials.email', confirmed_orders: { $size: { $filter: { input: '$orders', as: 'order', cond: { $eq: ['$$order.order_status', 'confirmed'] } } } } } }",
            "{ $sort: { confirmed_orders: -1 } }",
            "{ $limit: 10 }"
    })
    List<TopCustomerDTO> getTopCustomers();

    /**
     * Appends a custom Point of Interest to a specific confirmed order.
     * Translates a Neo4j geospatial node into the user's MongoDB order history.
     */
    @Query("{ '_id': ?0, 'orders.order_id': ?1 }")
    @Update("{ '$push': { 'orders.$.custom_pois': ?2 } }")
    long addNeo4jPoiToOrder(ObjectId userId, ObjectId orderId, CustomPoiDTO poiDetails);
}
