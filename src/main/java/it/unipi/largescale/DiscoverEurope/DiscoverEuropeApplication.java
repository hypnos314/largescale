package it.unipi.largescale.DiscoverEurope;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;
import org.springframework.data.neo4j.repository.config.EnableNeo4jRepositories;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableNeo4jRepositories(basePackages = "it.unipi.largescale.DiscoverEurope.repository.neo4j")
@EnableMongoRepositories(basePackages = "it.unipi.largescale.DiscoverEurope.repository.mongodb")
@EnableScheduling
public class DiscoverEuropeApplication {
	public static void main(String[] args) {
		SpringApplication.run(DiscoverEuropeApplication.class, args);
	}
}
