package Builders;

import Domain.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class OrderBuilder {
    private Customer customer;
    private String restaurantId;
    private Address deliveryAddress;
    private List<OrderLine> lineItems=new ArrayList<>();
    private Promotion promotion;
    private BigDecimal distanceKm;

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
    public OrderBuilder setCustomer(Customer customer) {
        this.customer = customer;
        return this;
    }

    public OrderBuilder setRestaurantId(String restaurantId) {
        this.restaurantId = restaurantId;
        return this;
    }

    public OrderBuilder setDeliveryAddress(Address deliveryAddress) {
        this.deliveryAddress = deliveryAddress;
        return this;
    }

    public OrderBuilder setLineItems(List<OrderLine> lineItems) {
        this.lineItems = lineItems;
        return this;
    }

    public OrderBuilder setPromotion(Promotion promotion) {
        this.promotion = promotion;
        return this;
    }
    public OrderBuilder setPromotion(BigDecimal distanceKm) {
        this.distanceKm = distanceKm;
        return this;
    }

    public Order build(){
        if (customer == null
                || restaurantId == null
                || deliveryAddress == null
                || lineItems.isEmpty()) {
            throw new IllegalStateException("Missing required order information");
        }
        return new Order(customer,restaurantId,deliveryAddress, lineItems,promotion,distanceKm);
    }
}
