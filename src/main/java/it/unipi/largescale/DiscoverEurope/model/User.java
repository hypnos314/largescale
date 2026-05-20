/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

package it.unipi.largescale.DiscoverEurope.model;

import it.unipi.largescale.DiscoverEurope.model.embeddedUser.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import java.util.List;

/**
 *
 * @author Beatrice
 */
@Data //genera automaticamente getter, setter, toString, equals e hashCode
@AllArgsConstructor //genera automaticamente un costruttore public User(String id, String name, String surname, ... , boolean admin) { ... }
@NoArgsConstructor //genera automaticamente un costruttore vuoto
@Document(collection = "users") //gli oggetti creati da questa classe devono essere trattati come documenti di MongoDB
public class User {
    @Id //indica che il campo sottostante (id) è la chiave primaria del documento
    private String id;
    private String role;
    private Credentials credentials;
    
    @Field("personal_info") //per personalizzare il nome del campo nel database
    private PersonalInfo personalInfo;
    
    @Field("identity_documents")
    private List<IdentityDocument> identityDocuments;
    
    private Cart cart;
    private List<Order> orders;
}
