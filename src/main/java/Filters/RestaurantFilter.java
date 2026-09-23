package Filters;

import Domain.Restaurant;

@FunctionalInterface
public interface RestaurantFilter {
    boolean matches(Restaurant restaurant);
    default RestaurantFilter and(RestaurantFilter other){
        return restaurant -> this.matches(restaurant)&& other.matches(restaurant);
    }
}
