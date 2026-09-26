package Domain;

import Exceptions.IllegalPromotionException;
import Exceptions.InvalidOrderTransitionException;
import Exceptions.PlatformException;
import OrderStatusObserver.EventPublisher;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Order {
    private static int nextId = 1;
    private final String id;
    private Customer customer;
    private String restaurantId;
    private Address deliveryAddress;
    private List<OrderLine> lineItems;
    private  LocalDateTime placedAt;
    private OrderStatus orderStatus;
    private Promotion promotion;
    private OrderPrice finalPrice;
    private String riderId;
    private LocalDateTime assignedAt;
    private LocalDateTime deliveredAt;
    private EventPublisher eventPublisher;
    private BigDecimal distanceKm;
    private boolean paid;

    public Order(Customer customer, String restaurantId,Address deliveryAddress,List<OrderLine>orderLines,Promotion promotion, BigDecimal distanceKm) {
        id="O-"+Integer.toString(nextId++);
        this.customer = customer;
        this.restaurantId = restaurantId;
        this.deliveryAddress = deliveryAddress;
        lineItems=new ArrayList<>(orderLines);
        placedAt =LocalDateTime.now();
        orderStatus=OrderStatus.PLACED;
        this.promotion=promotion;
        this.eventPublisher=new EventPublisher();
        this.distanceKm=distanceKm;
        this.paid=false;
    }

    public BigDecimal getTotalOrderUnits(){
        return lineItems.stream()
                .map(OrderLine::getQuantity)
                .reduce(BigDecimal.ZERO,BigDecimal::add);
    }
    public EventPublisher getEventPublisher() {
        return eventPublisher;
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
            throw new IllegalPromotionException("An order can have at most one promotion");
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
            throw new InvalidOrderTransitionException("Cannot change status from " + orderStatus + " to " + newStatus);
        }
        this.orderStatus=newStatus;
        eventPublisher.notifyListeners(this);
    }
    public boolean isValidTransition(OrderStatus newStatus){
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

    public BigDecimal getDistanceKm() {
        return distanceKm;
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

    public OrderPrice calculatePrice(){
        BigDecimal subTotal=BigDecimal.ZERO;
        for(var lineItem:lineItems){
            subTotal=subTotal.add(lineItem.calculateOrderLine());
        }

        BigDecimal deliveryFee=calculateDeliveryFee(this.distanceKm);

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
        this.finalPrice=new OrderPrice(subTotal,deliveryFee,serviceFee,promotionDiscount,total);
        return finalPrice;
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
        BigDecimal deliveryDiscount=customer.getLoyaltyTier().getDeliveryDiscount();
        deliveryFee=deliveryFee.subtract(deliveryFee.multiply(deliveryDiscount));

        return deliveryFee;
    }
    public void assignRider(String riderId){
        if(this.riderId!=null){
            throw new PlatformException("Order already assigned to a rider");
        }
        if(!isValidTransition(OrderStatus.ASSIGNED)){
            throw new InvalidOrderTransitionException("Cannot change status from " + orderStatus + " to " +OrderStatus.ASSIGNED.name() );
        }
        this.riderId = riderId;
        this.assignedAt=LocalDateTime.now();
        changeStatus(OrderStatus.ASSIGNED);
    }
    public void markDelivered(){
        if(!isValidTransition(OrderStatus.DELIVERED)){
            throw new InvalidOrderTransitionException("Cannot change status from " + orderStatus + " to " + OrderStatus.DELIVERED.name());
        }
        this.deliveredAt=LocalDateTime.now();
        changeStatus(OrderStatus.DELIVERED);
    }

    public void setPaid(){
        this.paid=true;
    }
    public boolean isPaid(){
        return paid;
    }
    public void markAsUnpaid(){
        this.paid=false;
    }
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Order order = (Order) o;
        return Objects.equals(id, order.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
    @Override
    public String toString() {
        return "Order " + id +
                " | Customer: " + customer.getName() +
                " | Restaurant: " + restaurantId +
                " | Delivery Address: " + deliveryAddress +
                " | Placed at: " + placedAt +
                "\n | Assigned at: " + assignedAt +
                " | Status: " + orderStatus +
                " | Total: " + calculatePrice().total() + " EGP" +
                " | paid: " + (isPaid()?"Yes":"No") +
                " | Distance: " + distanceKm + " km";
    }
}
