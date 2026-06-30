package it.unipi.largescale.DiscoverEurope.controller.mongodb;

import it.unipi.largescale.DiscoverEurope.DTO.mongodb.*;
import it.unipi.largescale.DiscoverEurope.service.mongodb.Admin_Analytics_Service_Mongo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

/**
 * Admin's REST Controller.
 */
@RestController
@RequestMapping("/api/admin/analytics")
public class Admin_Analytics_Controller_Mongo {

    @Autowired
    private Admin_Analytics_Service_Mongo adminAnalyticsService;

    /**
     * Retrieves a report of negative reviews filtered by specific negative words,
     * optionally filtered by a specific travel package.
     * * @param packageId the optional unique identifier of the travel package to filter by.
     * If null, returns the report for all packages.
     * @return a {@link ResponseEntity} containing a list of {@link NegativeReviewsReportDTO}
     */
    @GetMapping({"/negative-reviews", "/negative-reviews/{packageId}"})
    public ResponseEntity<List<NegativeReviewsReportDTO>> getNegativeReport(
            @PathVariable(required = false) String packageId) {
        return ResponseEntity.ok(adminAnalyticsService.getNegativeReviewsReport(packageId));
    }

    /**
     * Retrieves global statistics regarding surprise packages.
     * @return a {@link ResponseEntity} containing a {@link SurprisePackageStatsDTO} with performance metrics.
     */
    @GetMapping("/surprise-stats")
    public ResponseEntity<SurprisePackageStatsDTO> getSurpriseStats() {
        return ResponseEntity.ok(adminAnalyticsService.getSurpriseStats());
    }

    /**
     * Retrieves a report calculating the average travel budget divided by age ranges.
     * @return a {@link ResponseEntity} containing a list of {@link BudgetByAgeRangeDTO}
     * representing the spending habits per demographic.
     */
    @GetMapping("/budget-by-age-range")
    public ResponseEntity<List<BudgetByAgeRangeDTO>> getBudgetByAgeRange() {
        return ResponseEntity.ok(adminAnalyticsService.getAvgBudgetByAgeReport());
    }

    /**
     * Retrieves a list of hotels shared across multiple travel packages for a specific city.
     * @param city the name of the destination city to analyze.
     * @return a {@link ResponseEntity} containing a list of {@link SharedHotelDTO}.
     */
    @GetMapping("/shared-hotels/{city}")
    public ResponseEntity<List<SharedHotelDTO>> getSharedHotels(@PathVariable String city) {
        return ResponseEntity.ok(adminAnalyticsService.getSharedHotels(city));
    }

    /**
     * Calculates the total monthly revenue based on confirmed orders.
     * @return a {@link ResponseEntity} containing a list of {@link MonthlyRevenueDTO} aggregated by month.
     */
    @GetMapping("/monthly-revenue")
    public ResponseEntity<List<MonthlyRevenueDTO>> getMonthlyRevenue() {
            return ResponseEntity.ok(adminAnalyticsService.getMonthlyRevenue());
    }


    /**
     * Identifies top customers based on order count and applies a 10% discount for their next booking.
     * @return a {@link ResponseEntity} containing a list of the {@link TopCustomerDTO} who received the discount.
     */
    @PostMapping("/send-discounts")
    public ResponseEntity<List<TopCustomerDTO>> sendDiscountToTopCustomers() {
            return ResponseEntity.ok(adminAnalyticsService.sendDiscountToTopCustomers());
    }
}