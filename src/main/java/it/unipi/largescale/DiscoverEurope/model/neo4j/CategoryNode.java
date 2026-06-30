package it.unipi.largescale.DiscoverEurope.model.neo4j;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Node;

/**
 * Neo4j Node entity representing a generic classification category
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Node("Category")
public class CategoryNode {
    @Id
    private String name;
}