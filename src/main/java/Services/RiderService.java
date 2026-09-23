package Services;

import Domain.Order;
import Domain.Rider;
import Repositories.OrderRepository;
import Repositories.RiderRepository;

import java.util.ArrayList;
import java.util.List;

public class RiderService {
    private final RiderRepository riderRepository;
    private final OrderRepository orderRepository;

    public RiderService(RiderRepository riderRepository, OrderRepository orderRepository) {
        this.riderRepository = riderRepository;
        this.orderRepository = orderRepository;
    }

    public void markDelivered(String riderId){
        Rider rider=riderRepository.findById(riderId);
        Order order=rider.getActiveOrder();
        rider.completeDelivery();
        order.markDelivered();
        dispatchNextReadyOrder(rider);
    }

    private boolean dispatchNextReadyOrder(Rider rider){
        List<Order> temp=new ArrayList<>();
        while(true){
            Order order=orderRepository.removeNextReadyOrder();
            if(order==null)
                break;
            if(rider.canHandleOrder(order)){
                rider.assignOrder(order);
                order.assignRider(rider.getId());

                temp.forEach(orderRepository::addReadyOrder);

                return true;
            }
            temp.add(order);
        }
        temp.forEach(orderRepository::addReadyOrder);

        return false;
    }


}
