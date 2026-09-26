package Consoles;

import Domain.*;
import Exceptions.PlatformException;
import PromotionStrategies.FixedAmountPromotionStrategy;
import PromotionStrategies.FreeDeliveryPromotionStrategy;
import PromotionStrategies.PercentagePromotionStrategy;
import PromotionStrategies.PromotionStrategy;
import Services.*;
import Utils.InputHelper;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;

public class AdminConsole {
    private final InputHelper inputHelper;
    private final RestaurantService restaurantService;
    private final CustomerService customerService;
    private final RiderService riderService;
    private final PromotionService promotionService;
    private final ReportService reportService;
    private final AuditLog auditLog;
    public AdminConsole(InputHelper inputHelper, RestaurantService restaurantService, CustomerService customerService,
                        RiderService riderService, PromotionService promotionService,
                        ReportService reportService, AuditLog auditLog) {
        this.inputHelper = inputHelper;
        this.restaurantService = restaurantService;
        this.customerService = customerService;
        this.riderService = riderService;
        this.promotionService = promotionService;
        this.reportService = reportService;
        this.auditLog = auditLog;
    }

    public void start() {
        int choice;
        do {
            showMenu();
            choice = inputHelper.readInt("Choose an option: ");

            try {
                switch (choice) {
                    case 1 -> addRestaurant();
                    case 2 -> removeRestaurant();
                    case 3 -> toggleRestaurantStatus();
                    case 4 -> addCustomer();
                    case 5 -> removeCustomer();
                    case 6 -> addRider();
                    case 7 -> removeRider();
                    case 8 -> createPromotion();
                    case 9 -> runReports();
                    case 10 -> viewPlatformStatistics();
                    case 11-> showLogs();
                    case 0 -> System.out.println("Ciao!");
                    default -> System.out.println("Invalid choice. Please try again.");
                }
            } catch (PlatformException e) {
                System.out.println(e.getMessage());
            }

        } while (choice != 0);
    }

    private void toggleRestaurantStatus() {
        String restaurantId=inputHelper.readString("Restaurant ID: ");
        boolean isOpen = restaurantService.toggleRestaurantStatus(restaurantId);
        System.out.println("Toggled successfully, now it is "+(isOpen?"Opened":"Clsoed"));
    }

    private void showLogs() {
        auditLog.showLogs();
    }

    private void viewPlatformStatistics() {
        int totalRestaurants=reportService.getTotalNumberOfRestaurants();
        Long totalOpenRestaurants= reportService.getTotalNumberOfOpenRestaurants();

        int totalCustomers=reportService.getTotalNumberOfCustomers();

        int totalRiders=reportService.getTotalNumberOfRiders();
        Long availableRiders=reportService.getNumberOfAvailableRiders();

        int totalOrders=reportService.getTotalNumberOfOrders();
        Map<OrderStatus,Long>ordersPerStatus = reportService.getCountOfEachOrderStatus();

        System.out.println("\n===== Platform Statistics =====");

        System.out.println("\n--- Restaurants ---");
        System.out.println("Total Restaurants : " + totalRestaurants);
        System.out.println("Open Restaurants  : " + totalOpenRestaurants);

        System.out.println("\n--- Customers ---");
        System.out.println("Total Customers   : " + totalCustomers);

        System.out.println("\n--- Riders ---");
        System.out.println("Total Riders      : " + totalRiders);
        System.out.println("Available Riders  : " + availableRiders);

        System.out.println("\n--- Orders ---");
        System.out.println("Total Orders      : " + totalOrders);

        System.out.println("\nOrders by Status:");
        for (OrderStatus status : OrderStatus.values()) {
            System.out.println(status + " : " +
                    ordersPerStatus.getOrDefault(status, 0L));
        }

    }

    private void runReports() {
        ReportConsole reportConsole = new ReportConsole(inputHelper, reportService);
        reportConsole.start();
    }
    private void createPromotion(){
        System.out.println("Enter the promotion info ");
        String code=inputHelper.readString("Promo Code: ");
        BigDecimal minimumSubTotal=inputHelper.readBigDecimal("Minimum subtotal to apply the promotion: ");
        LocalDate expiryDate=inputHelper.readLocalDate("Expiry date: ");
        boolean isDistrictRestricted=inputHelper.readYesNo("Restrict promotion to a specific district?");
        String restrictedDistrict=null;
        if(isDistrictRestricted){
            restrictedDistrict=inputHelper.readString("Enter the restricted district: ");
        }
        boolean firstTimeCustomersRestriction= inputHelper.readYesNo("Restrict to first-time customers?");
        PromotionType promotionType=readPromotionType();
        PromotionStrategy promotionStrategy=null;
        switch (promotionType) {
            case PERCENTAGE -> {
                BigDecimal percentage =
                        inputHelper.readBigDecimal("Percentage (0-1): ");
                while (percentage.compareTo(BigDecimal.ZERO) < 0 ||
                        percentage.compareTo(BigDecimal.ONE) > 0) {
                    System.out.println("Percentage must be between 0 and 1.");
                    percentage = inputHelper.readBigDecimal("Percentage (0-1): ");
                }

                BigDecimal cap =
                        inputHelper.readBigDecimal("Maximum discount cap: ");
                while (cap.compareTo(BigDecimal.ZERO) < 0) {
                    System.out.println("Discount cap cannot be negative.");
                    cap = inputHelper.readBigDecimal("Maximum discount cap: ");
                }

                promotionStrategy =
                        new PercentagePromotionStrategy(percentage, cap);
            }

            case FIXED_AMOUNT -> {
                BigDecimal amount =
                        inputHelper.readBigDecimal("Discount amount: ");
                while (amount.compareTo(BigDecimal.ZERO) < 0) {
                    System.out.println("Discount amount cannot be negative.");
                    amount = inputHelper.readBigDecimal("Discount amount: ");
                }

                promotionStrategy =
                        new FixedAmountPromotionStrategy(amount);
            }

            case FREE_DELIVERY -> promotionStrategy =
                    new FreeDeliveryPromotionStrategy();
        }
        promotionService.createPromotion(code,minimumSubTotal,expiryDate,restrictedDistrict,
                firstTimeCustomersRestriction,promotionStrategy);
        System.out.println("Promotion created successfully.");
    }
    private void removeRider() {
        String riderId=inputHelper.readString("Rider ID: ");
        riderService.removeRider(riderId);
        System.out.println("Rider " + riderId + " removed successfully.");
    }
    private void addRider(){
        System.out.println("Enter the rider info ");
        String name=inputHelper.readString("Name: ");
        VehicleType vehicleType=readVehicleType();
        String district=inputHelper.readString("District: ");
        System.out.println("Now enter the vehicle info");
        BigDecimal maxRange=inputHelper.readBigDecimal("Max Range: ");
        BigDecimal maxOrdersUnits=inputHelper.readBigDecimal("Max order units it can hold: ");
        BigDecimal maxSpeed=inputHelper.readBigDecimal("Max speed: ");
        riderService.addRider(name,vehicleType,district,maxRange,maxOrdersUnits,maxSpeed);
        System.out.println("Rider added successfully.");
    }
    private void removeCustomer(){
        String customerId=inputHelper.readString("Customer ID: ");
        customerService.removeCustomer(customerId);
        System.out.println("Customer "+customerId+"removed successfully.");
    }

    private void addCustomer(){
        System.out.println("Enter the customer info ");
        String name=inputHelper.readString("Name: ");
        String phoneNumber=inputHelper.readString("Phone Number: ");
        BigDecimal walletBalance=inputHelper.readBigDecimal("Wallet Balance: ");
        List<Address>customerAddresses=new ArrayList<>();

        System.out.println("\nEnter the customer's address");
        customerAddresses.add(readAddress());
        while (inputHelper.readYesNo("Add another address?")) {

            customerAddresses.add(readAddress());
        }
        customerService.addCustomer(name,phoneNumber,walletBalance,customerAddresses);
        System.out.println("Customer added successfully.");
    }
    private Address readAddress(){
        String district = inputHelper.readString("District: ");
        String details = inputHelper.readString("Address details: ");

        if (district.isBlank()) {
            throw new PlatformException("District cannot be empty.");
        }
        return new Address(district,details);
    }

    private void removeRestaurant(){
        String restaurantId=inputHelper.readString("Restaurant ID: ");
        restaurantService.removeRestaurant(restaurantId);
        System.out.println("Restaurant "+restaurantId+" removed successfully");
    }
    private void addRestaurant(){
        System.out.println("Enter the restaurant info ");
        String name=inputHelper.readString("Name: ");
        String district=inputHelper.readString("District: ");
        double avgRating ;
       do{
            avgRating= inputHelper.readDouble("Average Rating: ");
            if(avgRating<0 ||avgRating>5){
                System.out.println("Invalid rating, Rating must be between 0.0 anf 5.0");
            }
        } while (avgRating<0 ||avgRating>5);
        Set<CuisineCategory> cuisines = readCuisines();
        restaurantService.addRestaurant(name,district,avgRating,cuisines);
        System.out.println("Restaurant added successfully.");
    }

    private PromotionType readPromotionType() {
        System.out.println("Choose promotion Type:");

        for (int i = 0; i < PromotionType.values().length; i++) {
            System.out.println((i + 1) + ". " + PromotionType.values()[i]);
        }
        int choice;
        do {
            choice = inputHelper.readInt("Choose promotion type: ");

            if (choice < 1 || choice > PromotionType.values().length) {
                System.out.println("Invalid choice.");
            }
        } while (choice < 1 || choice > PromotionType.values().length);
        return PromotionType.values()[choice - 1];
    }
    private VehicleType readVehicleType() {
        System.out.println("Choose Vehicle Type:");
        for (int i = 0; i < VehicleType.values().length; i++) {
            System.out.println((i + 1) + ". " + VehicleType.values()[i]);
        }
        int choice;
        do {
            choice = inputHelper.readInt("Choose vehicle type: ");

            if (choice < 1 || choice > VehicleType.values().length) {
                System.out.println("Invalid choice.");
            }
        } while (choice < 1 || choice > VehicleType.values().length);

        return VehicleType.values()[choice - 1];
    }
    private Set<CuisineCategory> readCuisines() {
        Set<CuisineCategory> cuisines = new HashSet<>();
        CuisineCategory[] categories = CuisineCategory.values();

        while (true) {
            System.out.println("Choose cuisine:");

            for (int i = 0; i < categories.length; i++) {
                System.out.println((i + 1) + ". " + categories[i]);
            }

            int choice = inputHelper.readInt("Enter cuisine number: ");

            if (choice < 1 || choice > categories.length) {
                System.out.println("Invalid cuisine number.");
                continue;
            }

            cuisines.add(categories[choice - 1]);

            if (!inputHelper.readYesNo("Add another cuisine?")) {
                return cuisines;
            }
        }
    }
    private void showMenu() {
        System.out.println("\n===== Admin Console =====");
        System.out.println("1. Add Restaurant");
        System.out.println("2. Remove Restaurant");
        System.out.println("3. Toggle Restaurant Status");
        System.out.println("4. Add Customer");
        System.out.println("5. Remove Customer");
        System.out.println("6. Add Rider");
        System.out.println("7. Remove Rider");
        System.out.println("8. Create Promotion");
        System.out.println("9. Run Reports");
        System.out.println("10. View Platform Statistics");
        System.out.println("11. View Audit Logs");
        System.out.println("0. Back");
    }
}
