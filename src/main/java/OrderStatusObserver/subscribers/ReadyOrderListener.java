package OrderStatusObserver.subscribers;

import Domain.Order;
import Domain.OrderStatus;
import Repositories.OrderRepository;

public class ReadyOrderListener implements  EventListener{
    private final OrderRepository orderRepository;

    public ReadyOrderListener(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    @Override
    public void update(Order order) {
        if(order.getOrderStatus()== OrderStatus.READY) {
            orderRepository.addReadyOrder(order);
        }
    }
}
