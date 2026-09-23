package Domain;

import Exceptions.IllegalPromotion;
import Exceptions.InvalidOrderTransition;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Order {
    private static int nextId = 1;
    private final String id;
    private Customer customer;
    private String restaurantId;
    private Address deliveryAddress;
    private List<OrderLine> lineItems;
    private final LocalDateTime placedAt;
    private OrderStatus orderStatus;
    private Promotion promotion;
    private OrderPrice finalPrice;
    private String riderId;
    private LocalDateTime assignedAt;
    private LocalDateTime deliveredAt;

    public Order(Customer customer, String restaurantId,Address deliveryAddress,List<OrderLine>orderLines,Promotion promotion) {
        id="O-"+Integer.toString(nextId++);
        this.customer = customer;
        this.restaurantId = restaurantId;
        this.deliveryAddress = deliveryAddress;
        lineItems=new ArrayList<>(orderLines);
        placedAt =LocalDateTime.now();
        orderStatus=OrderStatus.PLACED;
        this.promotion=promotion;
    }

    public String getId() {
        return id;
    }

    public String getRiderId() {
        return riderId;
    }

    public LocalDateTime getAssignedAt() {
        return assignedAt;
    }

    public LocalDateTime getDeliveredAt() {
        return deliveredAt;
    }

    public void applyPromotion(Promotion promotion) {
        if (this.promotion != null) {
            throw new IllegalPromotion("An order can have at most one promotion");
        }

        this.promotion = promotion;
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

    public Customer getCustomer() {
        return customer;
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

    public Promotion getPromotion() {
        return promotion;
    }

    public OrderPrice calculatePrice(BigDecimal distanceKm){
        BigDecimal subTotal=BigDecimal.ZERO;
        for(var lineItem:lineItems){
            subTotal=subTotal.add(lineItem.calculateOrderLine());
        }

        BigDecimal deliveryFee=calculateDeliveryFee(distanceKm);

        BigDecimal serviceFee=subTotal
                .multiply(PlatformConfig.getInstance().getServiceFeeRate())
                .setScale(2, RoundingMode.HALF_UP);

        BigDecimal promotionDiscount =BigDecimal.ZERO;
        if (promotion != null) {
            promotion.isApplicable(subTotal,
                    customer.isFirstTimeCustomer(),
                    deliveryAddress.district());

            promotionDiscount = promotion.getPromotionStrategy()
                    .calculatePromotionDiscount(subTotal, deliveryFee);
        }
        BigDecimal total= subTotal.add(deliveryFee).add(serviceFee).subtract(promotionDiscount);
        total=total.max(BigDecimal.ZERO);
        OrderPrice orderPrice=new OrderPrice(subTotal,deliveryFee,serviceFee,promotionDiscount,total);
        return orderPrice;
    }

    public void setFinalPrice(OrderPrice price){
        this.finalPrice=price;
    }
    public OrderPrice getFinalPrice() {
        return finalPrice;
    }

    private BigDecimal calculateDeliveryFee(BigDecimal distanceKm){
        BigDecimal deliveryFee=PlatformConfig.getInstance().getBaseDeliveryFee();
        BigDecimal remainingKms=distanceKm.subtract(BigDecimal.valueOf(3));
        if(remainingKms.compareTo(BigDecimal.ZERO)>0) {
            deliveryFee = deliveryFee.add(remainingKms.multiply(PlatformConfig.getInstance().getExtraKmFee()));
        }
        BigDecimal deliveryDiscount=customer.getLoyalityTier().getDeliveryDiscount();
        deliveryFee=deliveryFee.subtract(deliveryFee.multiply(deliveryDiscount));

        return deliveryFee;
    }
    public void assignRider(String riderId){
        if(riderId!=null){
            throw new IllegalStateException("Order already assigned to a rider");
        }
        this.riderId = riderId;
        this.assignedAt=LocalDateTime.now();
        changeStatus(OrderStatus.ASSIGNED);
    }
    public void markDelivered(){
        this.deliveredAt=LocalDateTime.now();
        changeStatus(OrderStatus.DELIVERED);
    }


}
