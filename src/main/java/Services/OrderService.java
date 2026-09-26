package Services;

import Builders.OrderBuilder;
import Domain.*;
import Exceptions.*;
import Repositories.CustomerRepository;
import Repositories.OrderRepository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import OrderStatusObserver.subscribers.*;
import Repositories.RestaurantRepository;
import Repositories.RiderRepository;


public class OrderService {
    private final OrderRepository orderRepository;
    private final AuditLog auditLog;
    private final RiderRepository riderRepository;
    private final RestaurantRepository restaurantRepository;


    public OrderService(OrderRepository orderRepository, AuditLog auditLog, RiderRepository riderRepository,
                      RestaurantRepository restaurantRepository) {
        this.orderRepository = orderRepository;
        this.auditLog=auditLog;
        this.riderRepository = riderRepository;
        this.restaurantRepository = restaurantRepository;
    }
    public int getTotalNumberOfOrders(){
        return orderRepository.getAllOrders().size();
    }
    public boolean dispatchNextReadyOrder(Rider rider){
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
    public BigDecimal payForOrder(String customerId,String orderId){
        Order order=orderRepository.findById(orderId);
        if(order==null){
            throw  new PlatformException("No order with this ID.");
        }
        if(!order.getCustomer().getId().equals(customerId)){
           throw new PlatformException("This order doesn't belong to this customer");
        }
        if(order.getOrderStatus()==OrderStatus.CANCELLED){
            throw new PlatformException("Cant pay for cancelled order.");
        }
        if(order.isPaid()) {
            throw new PlatformException("Order is paid already.");
        }
        Customer customer=order.getCustomer();
        if (order.getFinalPrice() == null) {
            throw new PlatformException("Order has not been priced.");
        }
        customer.deductMoney(order.getFinalPrice().total());
        order.setPaid();
        return customer.getWalletBalance();
    }
    public void placeOrder(Customer customer, String restaurantId, Address deliveryAddress, List<OrderLine> lineItems,
                           BigDecimal distance,Promotion promotion){

        Restaurant restaurant = restaurantRepository.findById(restaurantId);
        if (restaurant == null) {
            throw new PlatformException("There isn't restaurant with this ID.\"");
        }
        if (!restaurant.isOpen()) {
            throw new RestaurantClosedException("This restaurant is closed.");
        }
        if (!customer.getAddresses().contains(deliveryAddress)) {
            throw new PlatformException("Delivery address does not belong to customer.");
        }
        OrderBuilder orderBuilder=new OrderBuilder().setCustomer(customer)
                .setRestaurantId(restaurantId)
                .setDeliveryAddress(deliveryAddress)
                .setDistanceKm(distance)
                .setPromotion(promotion);

        for(var item:lineItems){
            orderBuilder.addLineItem(item.getMenuItem(),item.getQuantity());
        }
        Order order=orderBuilder.build();
        order.calculatePrice();
        System.out.println("Order's price: "+order.getFinalPrice());
        if(customer.getWalletBalance().compareTo(order.getFinalPrice().total())<0){
            throw new InsufficientWalletException("Insufficient funds");
        }
        for (OrderLine line : order.getLineItems()) {
            if (restaurant.getMenu().findById(line.getMenuItem().getId()) == null) {
                throw new UnavailableItemException("Item does not belong to this restaurant: " + line.getMenuItem().getName());
            }
            if (!line.getMenuItem().isAvailable()) {
                throw new UnavailableItemException("Item is not available: " + line.getMenuItem().getName());
            }

            if (!line.getMenuItem().hasEnoughStock(line.getQuantity().doubleValue())) {
                throw new StockShortageException("Insufficient stock for: " + line.getMenuItem().getName());
            }
        }
        for (OrderLine line : order.getLineItems()) {
            line.getMenuItem().decreaseStock(line.getQuantity().doubleValue());
        }
        order.getEventPublisher().subscribe(new CustomerNotificationListener());
        order.getEventPublisher().subscribe(new AuditLogListener(auditLog));
        order.getEventPublisher().subscribe(new ReadyOrderListener(orderRepository));
        order.getEventPublisher().subscribe(new RiderDashboardListener(PlatformConfig.getInstance(),riderRepository));
        order.getEventPublisher().subscribe(new StatisticsListener(riderRepository,restaurantRepository));
        orderRepository.addOrder(order);
        customer.incrementOrderCount();
        System.out.println("Order "+ order.getId()+" placed successfully");

    }
    public Order trackOrder(String customerId,String orderId){
        Order order=orderRepository.findById(orderId);
        if(order==null) {
            throw new PlatformException("No order with this ID");
        }
        if(!order.getCustomer().getId().equals(customerId)){
            throw new PlatformException("This order doesn't belong to this customer");
        }
        return order;
    }
    public void cancelOrder(String customerId,String orderId){
        Order order=orderRepository.findById(orderId);
        if(order==null){
            throw new PlatformException("No order with this ID");
        }
        if(!order.getCustomer().getId().equals(customerId)){
            throw new PlatformException("This order doesn't belong to this customer");
        }
        OrderStatus orderStatus=order.getOrderStatus();
        if(orderStatus==OrderStatus.CANCELLED){
            System.out.println("It is already cancelled before.");
            return;
        }
        if(!order.isValidTransition(OrderStatus.CANCELLED)){
            throw new PlatformException("This order cant be cancelled.");
        }

        if(orderStatus==OrderStatus.READY){
            orderRepository.removeReadyOrder(orderId);
        }
        if(order.isPaid()){
            order.getCustomer().addMoney(order.getFinalPrice().total());
            order.markAsUnpaid();
        }
        order.changeStatus(OrderStatus.CANCELLED);
        System.out.println("Canceled Successfully");
    }
}
