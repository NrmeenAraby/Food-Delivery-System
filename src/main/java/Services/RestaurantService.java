package Services;

import Domain.*;
import Exceptions.PlatformException;
import Exceptions.RestaurantNotFoundException;
import Filters.RestaurantFilter;
import Repositories.OrderRepository;
import Repositories.RestaurantRepository;

import java.util.List;
import java.util.Set;

public class RestaurantService {
    private final RestaurantRepository restaurantRepository;
    private final OrderRepository orderRepository;

    public RestaurantService(RestaurantRepository restaurantRepository, OrderRepository orderRepository) {
        this.restaurantRepository = restaurantRepository;
        this.orderRepository = orderRepository;
    }
    public boolean toggleRestaurantStatus(String restaurantId) {
        Restaurant restaurant=restaurantRepository.findById(restaurantId);
        if(restaurant==null){
            throw new PlatformException("No restaurant with this ID");
        }
        restaurant.updateRestaurantStatus(!restaurant.getStatus());
        return restaurant.isOpen();
    }

    public void removeRestaurant(String restaurantId){
        Restaurant restaurant=restaurantRepository.findById(restaurantId);
        if(restaurant==null){
            throw new PlatformException("No restaurant with this ID");
        }
        boolean hasActiveOrder=orderRepository.getAllOrders().stream()
                        .anyMatch(order -> order.getRestaurantId().equals(restaurantId)
                        && order.getOrderStatus()!=OrderStatus.DELIVERED
                        && order.getOrderStatus()!=OrderStatus.CANCELLED);
        if(hasActiveOrder){
            throw new PlatformException("Cannot remove restaurant with active orders.");
        }
        restaurantRepository.removeRestaurant(restaurantId);
    }
    public void addRestaurant(String name, String district, double avgRating, Set<CuisineCategory>cuisineCategories){

        if(cuisineCategories.isEmpty()){
            throw new PlatformException("Restaurant must have at least one cuisine.");
        }
        Restaurant restaurant=new Restaurant(name,district,avgRating);
        for(var cuisine: cuisineCategories){
            restaurant.addCuisineCategory(cuisine);
        }
        restaurantRepository.addRestaurant(restaurant);
        System.out.println("Restaurant "+restaurant.getId()+" added successfully.");
    }

    public List<Order> getOrdersWithSpecificStatus(String restaurantId,OrderStatus orderStatus){
       return orderRepository.getAllOrders().stream()
               .filter(order -> order.getRestaurantId().equals(restaurantId))
               .filter(order -> order.getOrderStatus().equals(orderStatus))
                .toList();

    }

    public List<Restaurant> searchByCriteria(SearchCriteria searchCriteria){
       RestaurantFilter restaurantFilter=restaurant->true;

       if(searchCriteria.getDistrict()!=null){
           restaurantFilter=restaurantFilter.and(r-> r.getDistrict().equalsIgnoreCase(searchCriteria.getDistrict()));
       }

       if(searchCriteria.getCuisineCategory()!=null){
          restaurantFilter= restaurantFilter.and(r->r.getCuisineCategories().contains(searchCriteria.getCuisineCategory()));
       }
       if(searchCriteria.getMinimumRating()!=null){
           restaurantFilter=restaurantFilter.and(r->r.getAvgRating()>=searchCriteria.getMinimumRating());
       }

       if(searchCriteria.getPriceCeiling()!=null){
           restaurantFilter=restaurantFilter.and(
                   r->r.getMenu().getItems().stream()
                           .allMatch( item -> item.calculateItemPrice().compareTo(searchCriteria.getPriceCeiling())<=0 )
           );
       }
        return restaurantRepository.searchByCriteria(restaurantFilter);
    }
    public List<MenuItem> getMenu(String restaurantId){
        Restaurant restaurant=restaurantRepository.findById(restaurantId);
        if(restaurant==null){
            throw new RestaurantNotFoundException("No restaurant found with this ID: " + restaurantId);
        }
        return restaurant.getMenu().getItems();
    }
    public List<Restaurant> freeTextSearch(String freeTxt){
        String keyword=freeTxt.toUpperCase();
        RestaurantFilter restaurantFilter= r->r.getName().toUpperCase().contains(keyword)
                || r.getCuisineCategories().stream()
                .anyMatch(c->c.name().contains(keyword));
        return restaurantRepository.searchByCriteria(restaurantFilter);
    }
    public Restaurant findById(String restaurantId){
        return  restaurantRepository.findById(restaurantId);
    }

    public List<Restaurant> getAllRestaurants() {
        return restaurantRepository.getAllRestaurants();
    }
}
