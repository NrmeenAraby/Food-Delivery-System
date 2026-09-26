package Consoles;

import Domain.*;
import Exceptions.PlatformException;
import Repositories.CustomerOrderHistoryReport;
import Services.ReportService;
import Services.RiderDeliveryReport;
import Utils.InputHelper;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class ReportConsole {
    private final InputHelper inputHelper;
    private final ReportService reportService;

    public ReportConsole(InputHelper inputHelper, ReportService reportService) {
        this.inputHelper = inputHelper;
        this.reportService = reportService;
    }

    public void start() {
        int choice;

        do {
            showMenu();
            choice = inputHelper.readInt("Choose an option: ");

            try {
                switch (choice) {
                    case 1 -> totalRevenueByDateRange();
                    case 2 -> topFiveRestaurantsByMonth();
                    case 3 -> averageOrderValueByDistrict();
                    case 4 -> highRatedRestaurants();
                    case 5 -> ordersByCurrentStatus();
                    case 6 -> riderDeliveryStatistics();
                    case 7 -> mostFrequentlyOrderedItem();
                    case 8 -> customerOrderHistory();
                    case 9 -> peakOrderingHour();
                    case 10 -> customersNotOrderedIn30Days();
                    case 0 -> System.out.println("Back...");
                    default -> System.out.println("Invalid choice. Please try again.");
                }
            } catch (PlatformException e) {
                System.out.println(e.getMessage());
            }

        } while (choice != 0);
    }

    private void showMenu() {
        System.out.println("\n===== Reports =====");
        System.out.println("1. Total Revenue by Date Range");
        System.out.println("2. Top 5 Restaurants by Monthly Revenue");
        System.out.println("3. Average Order Value by District");
        System.out.println("4. High-Rated Restaurants with at least 20 orders");
        System.out.println("5. Orders by Current Status");
        System.out.println("6. Rider Delivery Statistics");
        System.out.println("7. Most Frequently Ordered Menu Item");
        System.out.println("8. Customer Order History");
        System.out.println("9. Peak Ordering Hour");
        System.out.println("10. Customers Who Haven't Ordered in 30 Days");
        System.out.println("0. Back");
    }

    private void totalRevenueByDateRange() {
        LocalDate from = inputHelper.readLocalDate("From date (YYYY-MM-DD): ");
        LocalDate to = inputHelper.readLocalDate("To date (YYYY-MM-DD): ");

        if (from.isAfter(to)) {
            System.out.println("From date cannot be after to date.");
            return;
        }

        BigDecimal revenue = reportService.getTotalRevenue(from, to);

        System.out.println("\n===== Total Revenue =====");
        System.out.println("From: " + from);
        System.out.println("To: " + to);
        System.out.println("Total Revenue: " + revenue + " EGP");
    }

    private void topFiveRestaurantsByMonth() {
        int year = inputHelper.readInt("Year: ");
        int month;

        do {
            month = inputHelper.readInt("Month (1-12): ");

            if (month < 1 || month > 12) {
                System.out.println("Invalid month.");
            }
        } while (month < 1 || month > 12);

        YearMonth yearMonth = YearMonth.of(year, month);

        var restaurants = reportService.getTopFiveRestaurantsByRevenue(yearMonth);

        System.out.println("\n===== Top 5 Restaurants =====");
        if (restaurants.isEmpty()) {
            System.out.println("No revenue found for " + yearMonth);
            return;
        }
        int rank = 1;
        for (var entry : restaurants) {
            System.out.println(
                    rank++ + ". " + entry.getKey().getName() +
                            " - " + entry.getValue() + " EGP"
            );
        }
    }

    private void averageOrderValueByDistrict() {
        Map<String, Double> result = reportService.getAvgOrderValuePerDistrict();

        System.out.println("\n===== Average Order Value By District =====");
        if (result.isEmpty()) {
            System.out.println("No completed orders found.");
            return;
        }
        result.forEach((district, average) -> System.out.printf("%s : %.2f EGP%n", district, average));
    }

    private void highRatedRestaurants() {
        var restaurants = reportService.topRatingAndTwentyOrders();

        System.out.println("\n===== High-Rated Restaurants =====");

        if (restaurants.isEmpty()) {
            System.out.println("No restaurants match the criteria.");
            return;
        }
        for(Restaurant restaurant:restaurants){
            System.out.printf("%s | Rating: %.2f | Completed Orders: %d%n",
                    restaurant.getName(),restaurant.getAvgRating(),restaurant.getCompletedOrders());
        }
    }

    private void ordersByCurrentStatus() {
        Map<OrderStatus, Long> result = reportService.getCountOfEachOrderStatus();
        System.out.println("\n===== Orders By Status =====");
        for (OrderStatus status : OrderStatus.values()) {
            System.out.println(status + " : " + result.getOrDefault(status, 0L));
        }
    }

    private void riderDeliveryStatistics() {
        var reports = reportService.getRiderCompleteDeliveriesAndAvgDuration();
        System.out.println("\n===== Rider Delivery Statistics =====");

        if (reports.isEmpty()) {
            System.out.println("No riders found.");
            return;
        }

        for (RiderDeliveryReport report : reports) {
            System.out.println("Rider: " + report.riderId());
            System.out.println("Completed Deliveries: " + report.getOrdersSize());
            Duration duration = report.avgDeliveryDuration();

            System.out.println(
                    "Average Delivery Duration: " + duration.toMinutesPart() + " minutes "
                            + duration.toSecondsPart() + " seconds");
            System.out.println("-------------------------");
        }
    }

    private void mostFrequentlyOrderedItem() {
        Optional<MenuItem> result = reportService.getMostFrequentlyMenuItem();
        System.out.println("\n===== Most Frequently Ordered Item =====");
        if (result.isEmpty()) {
            System.out.println("No orders exist, so there is no most frequently ordered item.");
            return;
        }
        System.out.println("Item: " + result.get().getName());
    }

    private void customerOrderHistory() {
        String customerId = inputHelper.readString("Customer ID: ");
        CustomerOrderHistoryReport report = reportService.getCustomerOrderHistoryAndTotalSpent(customerId);
        System.out.println("\n===== Customer Order History =====");
        if (report.orders().isEmpty()) {
            System.out.println("This customer has no orders.");
        } else {
            for (Order order : report.orders()) {
                System.out.println(order);
            }
        }
        System.out.println("\nTotal Spent: " + report.totalSpent() + " EGP");
    }

    private void peakOrderingHour() {
        Optional<Integer> result = reportService.getPeakOrderingHour();
        System.out.println("\n===== Peak Ordering Hour =====");
        if (result.isEmpty()) {
            System.out.println("No orders exist.");
            return;
        }
        int hour = result.get();
        System.out.printf("Peak ordering hour: %02d:00 - %02d:00%n", hour, hour + 1);
    }

    private void customersNotOrderedIn30Days() {
        List<Customer> customers = reportService.getIdleCustomers();
        System.out.println("\n===== Customers Who Haven't Ordered In 30 Days =====");
        if (customers.isEmpty()) {
            System.out.println("No idle customers to show");
            return;
        }
        for (Customer customer : customers) {
            System.out.println(customer.getId() + " | " + customer.getName());
        }
    }
}