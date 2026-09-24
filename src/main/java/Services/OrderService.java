package Services;

import Domain.Order;
import Repositories.OrderRepository;

public class OrderService {
    private final OrderRepository orderRepository;

    public OrderService(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }
    public void addOrder(Order order){
        orderRepository.addOrder(order);
    }
}
