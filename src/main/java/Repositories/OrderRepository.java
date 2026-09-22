package Repositories;


import Domain.LoyaltyTier;
import Domain.Order;

import java.util.Comparator;
import java.util.HashMap;
import java.util.PriorityQueue;

public class OrderRepository {
    private final HashMap<String, Order> orders =new HashMap<>();
    private final Comparator<Order> priorityQueueComparator=Comparator.comparing((Order o)->o.getCustomer().getLoyalityTier()==LoyaltyTier.Gold)
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
}
