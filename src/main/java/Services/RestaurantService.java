package Services;

import Domain.Restaurant;
import Domain.SearchCriteria;
import Filters.RestaurantFilter;
import Repositories.RestaurantRepository;

import java.util.List;

public class RestaurantService {
    private final RestaurantRepository restaurantRepository;

    public RestaurantService(RestaurantRepository restaurantRepository) {
        this.restaurantRepository = restaurantRepository;
    }

    public List<Restaurant> searchByCriteria(SearchCriteria searchCriteria){
       RestaurantFilter restaurantFilter=restaurant->true;

       if(searchCriteria.getDistrict()!=null){
           restaurantFilter=restaurantFilter.and(
                   r-> r.getDistrict().equalsIgnoreCase(searchCriteria.getDistrict())
           );
       }

       if(searchCriteria.getCuisineCategory()!=null){
          restaurantFilter= restaurantFilter.and(
                   r->r.getCuisineCategories().contains(searchCriteria.getCuisineCategory())
           );
       }
       if(searchCriteria.getMinimumRating()!=null){
           restaurantFilter=restaurantFilter.and(
                   r->r.getAvgRating()>=searchCriteria.getMinimumRating()
           );
       }

       if(searchCriteria.getPriceCeiling()!=null){
           restaurantFilter=restaurantFilter.and(
                   r->r.getMenu().getItems().stream()
                           .allMatch( item -> item.calculateItemPrice().compareTo(searchCriteria.getPriceCeiling())<=0 )
           );
       }
        return restaurantRepository.searchByCriteria(restaurantFilter);
    }
    public List<Restaurant> freeTextSearch(String freeTxt){
        String keyword=freeTxt.toUpperCase();
        RestaurantFilter restaurantFilter= r->r.getName().toUpperCase().contains(keyword)
                || r.getCuisineCategories().stream()
                .anyMatch(c->c.name().contains(keyword));
        return restaurantRepository.searchByCriteria(restaurantFilter);
    }
}
