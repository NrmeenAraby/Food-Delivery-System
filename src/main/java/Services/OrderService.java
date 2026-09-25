package Services;

import Domain.*;
import Exceptions.PlatformException;
import Repositories.CustomerRepository;
import Repositories.OrderRepository;

import java.math.BigDecimal;


public class OrderService {
    private final OrderRepository orderRepository;
    private final CustomerRepository customerRepository;

    public OrderService(OrderRepository orderRepository, CustomerRepository customerRepository) {
        this.orderRepository = orderRepository;
        this.customerRepository = customerRepository;

    }
    public void addOrder(Order order){
        orderRepository.addOrder(order);
    }
    public BigDecimal payForOrder(String orderId){
        Order order=orderRepository.findById(orderId);
        if(order==null){
            throw  new PlatformException("No order with this ID.");
        }
        if(order.getOrderStatus()==OrderStatus.CANCELLED){
            throw new PlatformException("Cant pay for cancelled order.");
        }
        if(order.isPaid()) {
            throw new PlatformException("Order is paid already.");
        }
        BigDecimal total = order.calculatePrice().total();
        Customer customer=order.getCustomer();

        customer.deductMoney(total);
        order.setPaid();
        return customer.getWalletBalance();
    }
    public Order trackOrder(String orderId){
        Order order=orderRepository.findById(orderId);
        if(order==null) {
            throw new PlatformException("No order with this ID");
        }
        return order;
    }
    public void cancelOrder(String orderId){
        Order order=orderRepository.findById(orderId);
        if(order==null){
            throw new PlatformException("No order with this ID");
        }
        OrderStatus orderStatus=order.getOrderStatus();
        if(orderStatus==OrderStatus.CANCELLED){
            System.out.println("It is already cancelled before.");
            return;
        }
        if(orderStatus==OrderStatus.READY){
            orderRepository.removeReadyOrder(orderId);
        }
        if(order.isPaid()){
            BigDecimal total = order.calculatePrice().total();
            order.getCustomer().addMoney(total);
            order.markAsUnpaid();
        }
        order.changeStatus(OrderStatus.CANCELLED);
    }
}
