package Services;

import Domain.Restaurant;
import Filters.RestaurantFilter;
import Repositories.RestaurantRepository;

import java.util.List;

public class RestaurantService {
    private RestaurantRepository restaurantRepository;

    public void setRestaurantRepository(RestaurantRepository restaurantRepository) {
        this.restaurantRepository = restaurantRepository;
    }
    public List<Restaurant> searchByCriteria(RestaurantFilter restaurantFilter){
        return restaurantRepository.searchByCriteria(restaurantFilter);
    }
}
