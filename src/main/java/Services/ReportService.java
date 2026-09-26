package Services;

import Domain.*;
import Repositories.*;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.*;
import java.util.stream.Collectors;

public class ReportService {
    private final OrderRepository orderRepository;
    private final RestaurantRepository restaurantRepository;
    private final RiderRepository riderRepository;
    private final CustomerRepository customerRepository;

    public ReportService(OrderRepository orderRepository, RestaurantRepository restaurantRepository,
                         RiderRepository riderRepository, CustomerRepository customerRepository) {
        this.orderRepository = orderRepository;
        this.restaurantRepository = restaurantRepository;
        this.riderRepository = riderRepository;
        this.customerRepository = customerRepository;
    }

    public BigDecimal getTotalRevenue(LocalDate from, LocalDate to){
        return orderRepository.getAllOrders().stream()
                .filter(o->o.getOrderStatus()== OrderStatus.DELIVERED)
                .filter(o-> {
                            LocalDate date =o.getPlacedAt().toLocalDate();
                            return !date.isBefore(from) && !date.isAfter(to);
                        })
                .map(o->o.getFinalPrice().total())
                .reduce(BigDecimal.ZERO,BigDecimal::add);
    }
    public List<Map.Entry<Restaurant,BigDecimal>> getTopFiveRestaurantsByRevenue(YearMonth month){
        Map<String,BigDecimal> restaurantsRevenue = orderRepository.getAllOrders().stream()
                .filter(o->o.getOrderStatus()== OrderStatus.DELIVERED)
                .filter( o->{
                    YearMonth orderMonth=YearMonth.from(o.getPlacedAt());
                    return  orderMonth.equals(month);
                })
                .collect(Collectors.groupingBy(
                        Order::getRestaurantId,
                        Collectors.reducing(
                                BigDecimal.ZERO,
                                (Order o)->o.getFinalPrice().total(),
                                BigDecimal::add)));

        return restaurantsRevenue.entrySet().stream()
                .sorted(Map.Entry.<String,BigDecimal>comparingByValue().reversed())
                .limit(5)
                .map(entry-> Map.entry(
                        restaurantRepository.findById(entry.getKey()),
                        entry.getValue()
                ))
                .toList();
    }
    public Map<String, Double> getAvgOrderValuePerDistrict(){
        return orderRepository.getAllOrders().stream()
                .filter(order -> order.getOrderStatus()==OrderStatus.DELIVERED)
                .collect(Collectors.groupingBy(
                     o->o.getDeliveryAddress().district()
                ,Collectors.averagingDouble(
                        o->o.getFinalPrice().total().doubleValue()
                        )));
    }
    public List<Restaurant> topRatingAndTwentyOrders(){
        return restaurantRepository.getAllRestaurants().stream()
                .filter( restaurant->restaurant.getAvgRating()>4.5)
                .filter(restaurant -> restaurant.getCompletedOrders()>=20)
                .toList();
    }
    public Map<OrderStatus,Long> getCountOfEachOrderStatus(){
        return orderRepository.getAllOrders().stream()
                .collect(Collectors.groupingBy(Order::getOrderStatus
                ,Collectors.counting()));
    }
    public List<RiderDeliveryReport> getRiderCompleteDeliveriesAndAvgDuration(){
        Map<String,List<Order>> riderOrders= orderRepository.getAllOrders().stream()
                .filter(order->order.getOrderStatus()==OrderStatus.DELIVERED)
                .collect(Collectors.groupingBy(Order::getRiderId));

        return riderRepository.getAllRiders().stream()
                .map(
                        rider -> {
                            List<Order> orders=riderOrders.getOrDefault(rider.getId(), List.of());
                            double avgDuration=orders.stream()
                                    .mapToLong(
                                        order->Duration.between(order.getAssignedAt(),order.getDeliveredAt()).getSeconds()
                                    ).
                                    average()
                                    .orElse(0);
                            return new RiderDeliveryReport(rider.getId(),orders,Duration.ofSeconds((long) avgDuration));
                        }
                )
                .sorted(Comparator.comparing(RiderDeliveryReport::getOrdersSize).reversed())
                .toList();

    }
    public Optional<MenuItem> getMostFrequentlyMenuItem(){
        Map<MenuItem,BigDecimal> menuItems=orderRepository.getAllOrders().stream()
                .flatMap(o->o.getLineItems().stream())
                .collect(Collectors.groupingBy(
                        OrderLine::getMenuItem
                ,Collectors.reducing(
                                BigDecimal.ZERO,
                                OrderLine::getQuantity,
                                BigDecimal::add
                        )
                ));

        return  menuItems.entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey);

    }

    public CustomerOrderHistoryReport getCustomerOrderHistoryAndTotalSpent(String customerId){
      List<Order> customerOrders = orderRepository.getAllOrders().stream()
                .filter(o->o.getCustomer().getId().equals(customerId))
               .sorted(Comparator.comparing(Order::getPlacedAt).reversed())
                .toList();

        BigDecimal totalSpent=customerOrders.stream()
                .filter(Order::isPaid)
                .map(o->o.getFinalPrice().total())
                .reduce(BigDecimal.ZERO,BigDecimal::add);

        return new CustomerOrderHistoryReport(customerOrders,totalSpent);

    }
    public Optional<Integer> getPeakOrderingHour(){
        Map<Integer,Long> ordersHours=orderRepository.getAllOrders().stream()
                .collect(Collectors.groupingBy(o->o.getPlacedAt().getHour(),
                        Collectors.counting()));

        return ordersHours.entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey);
    }
    public List<Customer> getIdleCustomers() {
        LocalDateTime cutoff = LocalDateTime.now().minusDays(30);

        return customerRepository.getAllCustomers().stream()
                .filter(customer -> orderRepository.getAllOrders().stream()
                        .filter(order -> order.getCustomer().equals(customer))
                        .map(Order::getPlacedAt)
                        .max(LocalDateTime::compareTo)
                        .map(lastOrder -> lastOrder.isBefore(cutoff))
                        .orElse(true))
                .toList();
    }

    public int getTotalNumberOfRestaurants(){
        return restaurantRepository.getAllRestaurants().size();
    }
    public Long getTotalNumberOfOpenRestaurants() {
        return restaurantRepository.getAllRestaurants().stream()
                .filter(Restaurant::isOpen)
                .count();
    }
    public int getTotalNumberOfCustomers(){
        return customerRepository.getAllCustomers().size();
    }
    public int getTotalNumberOfRiders(){
        return riderRepository.getAllRiders().size();
    }
    public Long getNumberOfAvailableRiders() {
        return riderRepository.getAllRiders().stream()
                .filter(Rider::isAvailable)
                .count();
    }

    public int getTotalNumberOfOrders() {
        return  orderRepository.getAllOrders().size();
    }
}
