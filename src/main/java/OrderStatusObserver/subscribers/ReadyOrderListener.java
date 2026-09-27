package OrderStatusObserver.subscribers;

import Domain.Order;
import Domain.OrderStatus;
import Repositories.OrderRepository;
import Services.OrderService;

public class ReadyOrderListener implements  EventListener{
    private final OrderRepository orderRepository;
    private final OrderService orderService;

    public ReadyOrderListener(OrderRepository orderRepository,OrderService orderService) {
        this.orderRepository = orderRepository;
        this.orderService=orderService;
    }

    @Override
    public void update(Order order) {
        if(order.getOrderStatus()== OrderStatus.READY) {
            orderRepository.addReadyOrder(order);
            orderService.dispatchNextReadyOrder();
        }
    }
}
