package it.unipi.largescale.DiscoverEurope.DTO.mongodb;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Field;

/**
 * DTO representing aggregated monthly revenue statistics.
 * Aggregates the total revenue from all confirmed orders, grouped by year and month.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MonthlyRevenueDTO {

    @Field("Anno")
    private Integer year;
    @Field("Mese")
    private Integer month;
    @Field("Fatturato_Totale")
    private Double totalRevenue;
    @Field("Numero_Ordini")
    private Integer totalOrders;
}
