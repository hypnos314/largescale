package it.unipi.largescale.DiscoverEurope.DTO.mongodb;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Field;

/**
 * DTO representing the average travel budget categorized by age range.
 * Used in administrative dashboards to analyze spending habits across different demographics.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class BudgetByAgeRangeDTO {
    @Field("_id")
    private String ageRange;
    @Field("total_questionnaires")
    private int totalQuestionnaires;
    @Field("average_budget")
    private double averageBudget;
}
