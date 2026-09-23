package OrderStatusObserver.subscribers;

import Domain.Order;
import Domain.OrderStatus;
import Repositories.RestaurantRepository;
import Repositories.RiderRepository;

public class StatisticsListener implements EventListener{
    private final RiderRepository riderRepository;
    private final RestaurantRepository restaurantRepository;
    public StatisticsListener(RiderRepository riderRepository,RestaurantRepository restaurantRepository){
        this.riderRepository=riderRepository;
        this.restaurantRepository=restaurantRepository;

    }
    @Override
    public void update(Order order) {
        if(order.getOrderStatus()== OrderStatus.DELIVERED){
            order.getCustomer().incrementCompletedOrderCount();
            riderRepository.findById(order.getRiderId()).incrementCompletedDeliveries();
            restaurantRepository.findById(order.getRestaurantId()).incrementCompletedOrders();
        }
    }
}
