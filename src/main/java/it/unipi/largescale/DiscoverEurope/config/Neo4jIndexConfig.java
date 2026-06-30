package it.unipi.largescale.DiscoverEurope.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.data.neo4j.core.Neo4jClient;
import org.springframework.stereotype.Component;

@Component
public class Neo4jIndexConfig {

    @Autowired
    private Neo4jClient neo4jClient;

    @EventListener(ApplicationReadyEvent.class)
    public void initNeo4jIndexes() {

        // Index on user_id
        neo4jClient.query("CREATE INDEX user_id_idx IF NOT EXISTS FOR (u:User) ON (u.id)").run();

        // Index on birth_date
        neo4jClient.query("CREATE INDEX user_birth_idx IF NOT EXISTS FOR (u:User) ON (u.birth_date)").run();

        System.out.println("Neo4J indexes created");
    }
}