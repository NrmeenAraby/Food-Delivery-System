package Services;

import Builders.OrderBuilder;
import Domain.*;
import Exceptions.InvalidOrderTransitionException;
import Exceptions.PlatformException;
import Repositories.CustomerRepository;
import Repositories.OrderRepository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import OrderStatusObserver.subscribers.*;
import Repositories.RestaurantRepository;
import Repositories.RiderRepository;


public class OrderService {
    private final OrderRepository orderRepository;
    private AuditLog auditLog;
    private final RiderRepository riderRepository;
    private final PlatformConfig platformConfig;
    private final RestaurantRepository restaurantRepository;


    public OrderService(OrderRepository orderRepository, AuditLog auditLog, RiderRepository riderRepository,
                        PlatformConfig platformConfig, RestaurantRepository restaurantRepository) {
        this.orderRepository = orderRepository;
        this.auditLog=auditLog;
        this.riderRepository = riderRepository;
        this.platformConfig = platformConfig;
        this.restaurantRepository = restaurantRepository;
    }
    public List<Order> viewTodayOrders(String restaurantId) {
        LocalDate today = LocalDate.now();

        return orderRepository.getAllOrders().stream()
                .filter(order -> order.getRestaurantId().equals(restaurantId))
                .filter(order -> order.getPlacedAt().toLocalDate().equals(today))
                .toList();

    }
    public BigDecimal viewTodayRevenue(String restaurantId) {
        LocalDate today = LocalDate.now();
        return orderRepository.getAllOrders().stream()
                .filter(order -> order.getRestaurantId().equals(restaurantId))
                .filter(Order::isPaid)
                .filter(order -> order.getPlacedAt().toLocalDate().equals(today))
                .map(order -> order.getFinalPrice().total())
                .reduce(BigDecimal.ZERO,BigDecimal::add);
    }
    public void markReady(String orderId,String restaurantId){
        Order order=orderRepository.findById(orderId);
        if(order==null){
            throw  new PlatformException("No order with this ID.");
        }
        if(!order.getRestaurantId().equals(restaurantId)){
            throw new PlatformException("This order doesn't belong to this restaurant");
        }
        order.changeStatus(OrderStatus.READY);

    }
    public void markPreparing(String orderId,String restaurantId){
        Order order=orderRepository.findById(orderId);
        if(order==null){
            throw  new PlatformException("No order with this ID.");
        }
        if(!order.getRestaurantId().equals(restaurantId)){
            throw new PlatformException("This order doesn't belong to this restaurant");
        }
        order.changeStatus(OrderStatus.PREPARING);

    }
    public void rejectPendingOrder(String orderId,String restaurantId){
        Order order=orderRepository.findById(orderId);
        if(order==null){
            throw  new PlatformException("No order with this ID.");
        }
        if(!order.getRestaurantId().equals(restaurantId)){
            throw new PlatformException("This order doesn't belong to this restaurant");
        }
        if(order.getOrderStatus()!=OrderStatus.PLACED){
            throw new InvalidOrderTransitionException("This order isn't pending");
        }
        order.changeStatus(OrderStatus.CANCELLED);
    }
    public void acceptPendingOrder(String orderId,String restaurantId){
        Order order=orderRepository.findById(orderId);
        if(order==null){
            throw  new PlatformException("No order with this ID.");
        }
        if(!order.getRestaurantId().equals(restaurantId)){
            throw new PlatformException("This order doesn't belong to this restaurant");
        }
        if(order.getOrderStatus()!=OrderStatus.PLACED){
            throw new InvalidOrderTransitionException("Can't put this order status as accepted");
        }
        order.changeStatus(OrderStatus.ACCEPTED);
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
    public void placeOrder(Customer customer, String restaurantId, Address deliveryAddress, List<OrderLine> lineItems,
                           BigDecimal distance,Promotion promotion){
        OrderBuilder orderBuilder=new OrderBuilder().setCustomer(customer)
                .setRestaurantId(restaurantId)
                .setDeliveryAddress(deliveryAddress)
                .setDistanceKm(distance)
                .setPromotion(promotion);

        for(var item:lineItems){
            orderBuilder.addLineItem(item.getMenuItem(),item.getQuantity());
        }
        Order order=orderBuilder.build();
        for (OrderLine line : order.getLineItems()) {
            line.getMenuItem().decreaseStock(line.getQuantity().doubleValue());
        }
        order.getEventPublisher().subscribe(new CustomerNotificationListener());
        order.getEventPublisher().subscribe(new AuditLogListener(auditLog));
        order.getEventPublisher().subscribe(new ReadyOrderListener(orderRepository));
        order.getEventPublisher().subscribe(new RiderDashboardListener(platformConfig,riderRepository));
        order.getEventPublisher().subscribe(new StatisticsListener(riderRepository,restaurantRepository));
        orderRepository.addOrder(order);

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
