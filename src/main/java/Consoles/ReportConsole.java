package Consoles;

import Exceptions.PlatformException;
import Services.ReportService;
import Utils.InputHelper;

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
        System.out.println("4. High-Rated Restaurants");
        System.out.println("5. Orders by Current Status");
        System.out.println("6. Rider Delivery Statistics");
        System.out.println("7. Most Frequently Ordered Menu Item");
        System.out.println("8. Customer Order History");
        System.out.println("9. Peak Ordering Hour");
        System.out.println("10. Customers Who Haven't Ordered in 30 Days");
        System.out.println("0. Back");
    }

    private void totalRevenueByDateRange() {
        // later
    }

    private void topFiveRestaurantsByMonth() {
        // later
    }

    private void averageOrderValueByDistrict() {
        // later
    }

    private void highRatedRestaurants() {
        // later
    }

    private void ordersByCurrentStatus() {
        // later
    }

    private void riderDeliveryStatistics() {
        // later
    }

    private void mostFrequentlyOrderedItem() {
        // later
    }

    private void customerOrderHistory() {
        // later
    }

    private void peakOrderingHour() {
        // later
    }

    private void customersNotOrderedIn30Days() {
        // later
    }
}