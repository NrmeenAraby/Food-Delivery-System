package Consoles;

import Builders.OrderBuilder;
import Domain.*;
import Exceptions.*;
import Repositories.*;
import Services.CustomerService;
import Services.OrderService;
import Services.ReportService;
import Services.RestaurantService;
import Utils.InputHelper;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

public class CustomerConsole {
    private final InputHelper inputHelper;
    private final CustomerService customerService;
    private final RestaurantService restaurantService;
    private final OrderService orderService;
    private final PromotionRepository promotionRepository;
    private final ReportService reportService;
    private  String customerId;

    public CustomerConsole(InputHelper inputHelper,  CustomerService customerService,
                           RestaurantService restaurantService, OrderService orderService,
                           PromotionRepository promotionRepository, ReportService reportService) {
        this.inputHelper = inputHelper;
        this.customerService=customerService;
        this.restaurantService=restaurantService;
        this.orderService=orderService;
        this.promotionRepository = promotionRepository;
        this.reportService = reportService;
    }

    public void start() {
        this.customerId=inputHelper.readString("Customer ID: ");

        int choice;
        do {
            if(customerService.findById(customerId)==null){
                throw new PlatformException("No customer with this ID");
            }
            showMenu();
            choice = inputHelper.readInt("Choose an option: ");
            try{
            switch (choice) {
                case 1 -> browseRestaurants();
                case 2 -> searchRestaurants();
                case 3 -> viewMenu();
                case 4 -> placeOrder();
                case 5 -> payForOrder();
                case 6 -> trackOrder();
                case 7 -> cancelOrder();
                case 8 -> showOrderHistory();
                case 0 -> System.out.println("Ciao!");
                default -> System.out.println("Invalid choice. Please try again.");
            }
        }catch(PlatformException e){
                System.out.println(e.getMessage());
            }
        }while(choice!=0);
    }

    private void showOrderHistory() {
        CustomerOrderHistoryReport report =
                reportService.getCustomerOrderHistoryAndTotalSpent(customerId);

        System.out.println("\n===== Order History =====");

        List<Order> orders = report != null && report.orders() != null
                ? report.orders()
                : List.of();

        if (orders.isEmpty()) {
            System.out.println("No orders found.");
        } else {
            for (Order order : orders) {
                if (order == null) {
                    continue;
                }

                System.out.println(
                        "Order ID: " + order.getId()
                                + " | Restaurant: " + order.getRestaurantId()
                                + " | Status: " + order.getOrderStatus()
                                + " | Placed At: " + order.getPlacedAt()
                                + " | Total: " +
                                (order.getFinalPrice() != null
                                        ? order.getFinalPrice().total() + " EGP"
                                        : "N/A")
                );
            }
        }

        BigDecimal totalSpent = report != null && report.totalSpent() != null
                ? report.totalSpent()
                : BigDecimal.ZERO;

        System.out.println("-------------------------");
        System.out.println("Lifetime Total Spent: " + totalSpent + " EGP");
    }
    private void cancelOrder(){
        String orderId=inputHelper.readString("Enter the order ID: ");
        orderService.cancelOrder(orderId);
        System.out.println("Canceled Successfully");
    }

    private void trackOrder(){
        String orderId=inputHelper.readString("Enter the order ID: ");
        Order order=orderService.trackOrder(orderId);
        System.out.println("Status: "+order.getOrderStatus()+" , Elapsed time: "+ Duration.between(order.getPlacedAt(),LocalDateTime.now()));
    }
    private void payForOrder(){
        String orderId=inputHelper.readString("Enter the order ID: ");
        BigDecimal newBalance=orderService.payForOrder(orderId);
        System.out.println("Order Paid Successfully.");
        System.out.println("New wallet balance: "+newBalance+" EGP");
    }
    private void placeOrder() {
        System.out.println("Enter order info");

        Customer customer = customerService.findById(customerId);

        String restaurantId = inputHelper.readString("Restaurant ID: ");

        System.out.println("Choose delivery address:");
        List<Address> customerAddresses = customer.getAddresses();
        viewCustomerAddresses(customerAddresses);
        int choice;
        do {
            choice = inputHelper.readInt("Enter address number:");
        } while (choice <= 0 || choice > customerAddresses.size());

        Address deliveryAddress = customerAddresses.get((choice-1));

        List<MenuItem> menuItems = restaurantService.getMenu(restaurantId);
        List<OrderLine> lineItems = new ArrayList<>();
        int itemNumber;
        while (true) {
            Map<Integer, MenuItem> displayedItems = showGroupedMenuByCriteria(menuItems, MenuItem::getItemCategory);
            do {
                itemNumber = inputHelper.readInt("Choose Item(0 to exit): ");
            } while (itemNumber < 0 || itemNumber > menuItems.size());
            System.out.println("\n" + "0. Done");
            if (itemNumber == 0)
                break;
            MenuItem pickedMenuItem = displayedItems.get(itemNumber);
            if (pickedMenuItem == null) {
                System.out.println("Invalid item number.");
                continue;
            }

            double quantity = inputHelper.readDouble("Quantity: ");
            while (quantity <= 0.0) {
                System.out.println("Quantity must be greater than zero.");
                quantity = inputHelper.readDouble("Quantity: ");
            }

            lineItems.add(new OrderLine(pickedMenuItem, BigDecimal.valueOf(quantity)));
        }
        BigDecimal distance = inputHelper.readBigDecimal("Enter the estimated distance: ");
        while (distance.compareTo(BigDecimal.ZERO) <= 0) {
            System.out.println("Distance must be greater than zero");
            distance = inputHelper.readBigDecimal("Enter the estimated distance: ");
        }
        String promoCode = inputHelper.readString("Enter promo code (press Enter to skip): ");
        Promotion promotion=null;
        if (!promoCode.isBlank()) {
            promotion = promotionRepository.findByCode(promoCode);

            if (promotion == null) {
                throw new IllegalPromotionException("Invalid promo code.");
            }

            if (promotion.isExpired()) {
                throw new IllegalPromotionException("This promotion is expired.");
            }
        }
        orderService.placeOrder(customer, restaurantId,deliveryAddress,lineItems, distance,promotion);
        System.out.println("Order placed successfully");
    }
    private void viewCustomerAddresses(List<Address>addresses){
        for(int i=0;i<addresses.size();i++){
            Address address=addresses.get(i);
            System.out.println((i+1)+". "+address.details()+
                    ", "+address.district());
        }
    }
    private void viewMenu(){
        String restaurantId=inputHelper.readString("Enter the restaurant ID: ");
        try{
            List<MenuItem> menuItems = restaurantService.getMenu(restaurantId);
            if(menuItems.isEmpty()){
                System.out.println("No items available.");
                return;
            }
            showGroupedMenuByCriteria(menuItems, MenuItem::getMenuItemType);
        }
        catch(PlatformException e) {
            System.out.println(e.getMessage());
        }
    }
    private void searchRestaurants(){
        String freeTxt = inputHelper.readString("Search: ").trim();
        while(freeTxt.isBlank()) {
            System.out.println("Search cannot be empty.");
            freeTxt = inputHelper.readString("Enter: ");
        }
        SearchCriteria criteria=new SearchCriteria();
        criteria.setKeyword(freeTxt);
        customerService.saveSearch(customerId,criteria);
        List<Restaurant> filteredRestaurants=restaurantService.freeTextSearch(freeTxt);
        if(filteredRestaurants.isEmpty()){
            System.out.println("No restaurants found.");
            return;
        }
        showRestaurants(filteredRestaurants);
    }
    private void browseRestaurants(){
        SearchCriteria criteria = new SearchCriteria();

        String district = inputHelper.readString("District (press Enter to skip): ");
        if (!district.isBlank()) {
            criteria.setDistrict(district);
        }

        CuisineCategory cuisine = readCuisine();

        if (cuisine != null) {
            criteria.setCuisineCategory(cuisine);
        }

        Double minRating = inputHelper.readOptionalDouble(
                "Minimum rating (press Enter to skip): ");

        if (minRating != null) {
            criteria.setMinimumRating(minRating);
        }

        BigDecimal priceCeiling = inputHelper.readOptionalBigDecimal(
                "Price ceiling (press Enter to skip): ");

        if (priceCeiling != null) {
            criteria.setPriceCeiling(priceCeiling);
        }

        customerService.saveSearch(customerId,criteria);
        List<Restaurant> filteredRestaurants=restaurantService.searchByCriteria(criteria);
        if(filteredRestaurants.isEmpty()){
            System.out.println("No restaurants found.");
            return;
        }
        filteredRestaurants.sort(Comparator.comparing(Restaurant::getAvgRating)
                .reversed().thenComparing(Restaurant::getName));
        showRestaurants(filteredRestaurants);
    }


    private<T> Map<Integer, MenuItem> showGroupedMenuByCriteria(List<MenuItem>menuItems,
                                              Function<MenuItem,T>criteria){
        LinkedHashMap<T,List<MenuItem>> toShow = menuItems.stream()
                .collect(Collectors.groupingBy(criteria,
                        LinkedHashMap::new,
                        Collectors.toList()));

        Map<Integer, MenuItem> displayedItems = new LinkedHashMap<>();
        System.out.println("===== Menu =====");
        int idx=0;
        for(var entry: toShow.entrySet()){
            System.out.println( "===== "+entry.getKey()+"  ===== ");
            for(var item:entry.getValue()){
                idx++;
                displayedItems.put(idx, item);
                System.out.println(idx+". "+item.getName()
                        +" | "+item.calculateItemPrice()+" EGP "
                        +" | "+ item.getPreparationTimeMinutes() +" min "
                        +" | "+(item.isAvailable()?"Available":"Unavailable"));
                System.out.println();
            }
        }
        return displayedItems;
    }
    private void showRestaurants(List<Restaurant> restaurants){
                for(int i=0;i<restaurants.size();i++){
                    Restaurant restaurant=restaurants.get(i);
                    System.out.println((i+1)+". "+restaurant.getName()
                            + " | " + restaurant.getDistrict()
                            + " | " + restaurant.getAvgRating());
                }
    }
    private CuisineCategory readCuisine() {
        System.out.println("Available cuisines:");

        for (CuisineCategory cuisine : CuisineCategory.values()) {
            System.out.println("- " + cuisine);
        }

        while (true) {
            String input = inputHelper.readString(
                    "Cuisine (press Enter to skip): ");

            if (input.isBlank()) {
                return null;
            }

            try {
                return CuisineCategory.valueOf(input.toUpperCase());
            } catch (IllegalArgumentException e) {
                System.out.println("Invalid cuisine. Please choose from the list.");
            }
        }
    }
    private void showMenu() {
        System.out.println("\n===== Customer Menu =====");
        System.out.println("1. Browse Restaurants");
        System.out.println("2. Search Restaurants");
        System.out.println("3. View Menu");
        System.out.println("4. Place Order");
        System.out.println("5. Pay for Order");
        System.out.println("6. Track Order");
        System.out.println("7. Cancel Order");
        System.out.println("8. Order History");
        System.out.println("0. Back");
    }
}