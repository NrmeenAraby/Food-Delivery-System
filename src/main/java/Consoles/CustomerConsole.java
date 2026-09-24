package Consoles;

import Domain.CuisineCategory;
import Domain.Customer;
import Domain.Restaurant;
import Domain.SearchCriteria;
import Services.CustomerService;
import Services.RestaurantService;
import Utils.InputHelper;

import java.math.BigDecimal;
import java.util.Comparator;
import java.util.List;

public class CustomerConsole {
    private final InputHelper inputHelper;
    private final CustomerService customerService;
    private final RestaurantService restaurantService;
    private String customerId;

    public CustomerConsole(String customerId,InputHelper inputHelper,CustomerService customerService,RestaurantService restaurantService) {
        this.customerId=customerId;
        this.inputHelper = inputHelper;
        this.customerService=customerService;
        this.restaurantService=restaurantService;
    }

    public void start() {
        int choice;
        do {
            showMenu();
            choice = inputHelper.readInt("Choose an option: ");
            switch (choice) {
                case 1 -> browseRestaurants();
                case 2 -> searchRestaurants();
//                case 3 -> viewMenu();
//                case 4 -> placeOrder();
//                case 5 -> payForOrder();
//                case 6 -> trackOrder();
//                case 7 -> cancelOrder();
//                case 8 -> showOrderHistory();
                case 0 -> System.out.println("Ciao!");
                default -> System.out.println("Invalid choice. Please try again.");
            }
        }while(choice!=0);
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
        filteredRestaurants.sort(Comparator.comparing(Restaurant::getAvgRating).reversed());
        showRestaurants(filteredRestaurants);
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
    private void showRestaurants(List<Restaurant> restaurants){
                for(int i=0;i<restaurants.size();i++){
                    Restaurant restaurant=restaurants.get(i);
                    System.out.println((i+1)+". "+restaurant.getName()
                            + " | " + restaurant.getDistrict()
                            + " | " + restaurant.getAvgRating());
                }
    }
    private CuisineCategory readCuisine() {
        while (true) {
            String input = inputHelper.readString(
                    "Cuisine (press Enter to skip): ");

            if (input.isBlank()) {
                return null;
            }

            try {
                return CuisineCategory.valueOf(input.toUpperCase());
            } catch (IllegalArgumentException e) {
                System.out.println("Invalid cuisine. Please try again.");
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