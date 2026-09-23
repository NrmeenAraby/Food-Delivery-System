package Repositories;

import Domain.CuisineCategory;
import Domain.Order;
import Domain.Restaurant;

import java.util.*;

public class RestaurantRepository {
    private Map<String, Restaurant> restaurants=new HashMap<>();

    public void addRestaurant(Restaurant restaurant){
        restaurants.put(restaurant.getId(),restaurant);
    }
    public Restaurant findById(String restaurantId){
        return restaurants.get(restaurantId);
    }
    public Set<CuisineCategory> getAllCuisines(){
        Set<CuisineCategory>cuisines=new HashSet<>();
        for(var restaurant:restaurants.values()){
            cuisines.addAll(restaurant.getCuisineCategories());
        }
        return Set.copyOf(cuisines);
    }
    public List<Restaurant> listRestaurantsSortedByRating(){
        Comparator<Restaurant> ratingSorted=Comparator.comparing(Restaurant::getAvgRating,Comparator.reverseOrder())
                .thenComparing(Restaurant::getName);
        return restaurants.values().stream()
                .sorted(ratingSorted)
                .toList();
    }
    public List<Restaurant> getAllRestaurants(){
        return restaurants.values().stream().toList();
    }
}
