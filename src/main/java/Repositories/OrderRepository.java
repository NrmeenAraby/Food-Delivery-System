package Repositories;


import Domain.LoyaltyTier;
import Domain.Order;

import java.util.*;

public class OrderRepository {
    private final HashMap<String, Order> orders =new HashMap<>();
    private final Comparator<Order> priorityQueueComparator=Comparator.
            comparing((Order o)->o.getCustomer().getLoyaltyTier()==LoyaltyTier.Gold)
            .reversed()
            .thenComparing(Order::getPlacedAt);
    private final PriorityQueue<Order> readyOrders=new PriorityQueue<>(priorityQueueComparator);
    public void addOrder(Order order){
        orders.put(order.getId(),order);
    }
    public Order showNextReadyOrder(){
        return readyOrders.peek();
    }
    public Order removeNextReadyOrder(){
        return readyOrders.poll();
    }
    public Order findById(String orderId){
        return orders.get(orderId);
    }
    public List<Order> getAllOrders(){
        return orders.values().stream().toList();
    }
    public void addReadyOrder(Order order){
        readyOrders.add(order);
    }
    public void removeReadyOrder(String orderid){
        Order order=orders.get(orderid);
        if(order!=null) {
            readyOrders.remove(order);
        }
    }
    public List<Order> getAllReadyOrders(){
        List<Order> orders = new ArrayList<>();
        PriorityQueue<Order> copy = new PriorityQueue<>(readyOrders);
        while (!copy.isEmpty()) {
            orders.add(copy.poll());
        }
        return List.copyOf(orders);
    }
}
