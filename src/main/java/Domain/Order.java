package Domain;

import Exceptions.InvalidOrderTransition;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Order {
    private static int nextId = 1;
    private final String id;
    private String customerId;
    private String restaurantId;
    private Address deliveryAddress;
    private List<OrderLine> lineItems;
    private final LocalDateTime placedAt;
    private OrderStatus orderStatus;

    public Order(String customerId, String restaurantId,Address deliveryAddress,List<OrderLine>orderLines) {
        id="O-"+Integer.toString(nextId++);
        this.customerId = customerId;
        this.restaurantId = restaurantId;
        this.deliveryAddress = deliveryAddress;
        lineItems=new ArrayList<>(orderLines);
        placedAt =LocalDateTime.now();
        orderStatus=OrderStatus.PLACED;
    }
    public void addLineItem(MenuItem item, BigDecimal quantity){
        for(int idx=0;idx<lineItems.size();idx++){
            OrderLine line=lineItems.get(idx);
            if(line.getMenuItem().equals(item)){
                BigDecimal newQuantity=line.getQuantity().add(quantity);
                lineItems.set(idx,new OrderLine(item,newQuantity));
                return;
            }
        }
        lineItems.add(new OrderLine(item,quantity));
    }
    public void changeStatus(OrderStatus newStatus){
        if(!isValidTransition(newStatus)){
            throw new InvalidOrderTransition("Cannot change status from " + orderStatus + " to " + newStatus);
        }
        this.orderStatus=newStatus;
    }
    private boolean isValidTransition(OrderStatus newStatus){
        return switch (orderStatus){
            case PLACED -> newStatus==OrderStatus.ACCEPTED
                    || newStatus==OrderStatus.CANCELLED;

            case ACCEPTED -> newStatus==OrderStatus.PREPARING
                    || newStatus==OrderStatus.CANCELLED;

            case PREPARING -> newStatus==OrderStatus.READY
                    || newStatus==OrderStatus.CANCELLED;

            case READY -> newStatus==OrderStatus.ASSIGNED
                    || newStatus==OrderStatus.CANCELLED;

            case ASSIGNED -> newStatus==OrderStatus.OUT_FOR_DELIVERY
                    ||newStatus==OrderStatus.CANCELLED;

            case OUT_FOR_DELIVERY -> newStatus==OrderStatus.DELIVERED;

            case DELIVERED, CANCELLED->false;
        };
    }

    public String getCustomerId() {
        return customerId;
    }

    public String getRestaurantId() {
        return restaurantId;
    }

    public Address getDeliveryAddress() {
        return deliveryAddress;
    }

    public List<OrderLine> getLineItems() {
        return List.copyOf(lineItems);
    }

    public LocalDateTime getPlacedAt() {
        return placedAt;
    }

    public OrderStatus getOrderStatus() {
        return orderStatus;
    }
}
