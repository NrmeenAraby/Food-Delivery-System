package OrderStatusObserver.subscribers;

import Domain.Order;
import Domain.OrderStatus;
import Repositories.OrderRepository;
import Repositories.RiderRepository;

public class RiderDispatcherListener implements EventListener{
    private final RiderRepository riderRepository;
    private final OrderRepository orderRepository;

    public RiderDispatcherListener(
            RiderRepository riderRepository,
            OrderRepository orderRepository) {
        this.riderRepository = riderRepository;
        this.orderRepository = orderRepository;
    }
    @Override
    public void update(Order order) {
        if(order.getOrderStatus()!= OrderStatus.READY) {
            return;
        }
        for(var rider: riderRepository.getAllRiders()){
            if(rider.canHandleOrder(order)){
                order.assignRider(rider.getId());
                rider.assignOrder(order);
                return;
            }
        }
        orderRepository.addReadyOrder(order);
    }
}
