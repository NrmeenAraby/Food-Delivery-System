package Domain;

import DispatchRiderStrategies.DispatchStrategy;
import Exceptions.RiderBusyException;
import Factories.DispatchStrategyFactory;

import java.math.BigDecimal;

public class Rider {
    private static int nextId = 1;
    private final String id;
    private String name;
    private VehicleType vehicleType;
    private String district;
    private boolean availabilityStatus;
    private int completedDeliveriesCount;
    private Order activeOrder;
    private final DispatchStrategy dispatchStrategy;

    public Rider(String name, VehicleType vehicleType, String district,
                 BigDecimal maxRange,BigDecimal maxOrdersUnits, BigDecimal maxSpeed) {
        id="RD-"+Integer.toString(nextId++);
        this.name = name;
        this.vehicleType = vehicleType;
        this.district = district;
        availabilityStatus=true;
        completedDeliveriesCount=0;
        dispatchStrategy= DispatchStrategyFactory.createDispatchStrategy(vehicleType,maxRange,maxOrdersUnits,maxSpeed);
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public VehicleType getVehicleType() {
        return vehicleType;
    }

    public String getDistrict() {
        return district;
    }

    public boolean isAvailable() {
        return availabilityStatus;
    }

    public Order getActiveOrder() {
        return activeOrder;
    }
    public boolean hasActiveOrder(){
        return (activeOrder!=null);
    }

    public int getCompletedDeliveriesCount() {
        return completedDeliveriesCount;
    }

    public void updateAvailabilityStatus(boolean status){
        this.availabilityStatus=status;
    }
    public void incrementCompletedDeliveries(){
        completedDeliveriesCount++;
    }
    public void assignOrder(Order order){
        if(activeOrder!=null){
            throw new RiderBusyException("Rider already has an active order");
        }
        activeOrder=order;
    }
    public void cancelOrder(){
        activeOrder=null;
    }
    public void completeDelivery(){
        if(activeOrder==null) {
            throw new IllegalStateException("No active order.");
        }
        activeOrder=null;
    }
    public boolean canHandleOrder(Order order){
        return  isAvailable() && !hasActiveOrder() && dispatchStrategy.canAssign(order);
    }

}
