package Domain;

import DispatchRiderStrategies.DispatchStrategy;
import Exceptions.PlatformException;
import Exceptions.RiderBusyException;
import Factories.DispatchStrategyFactory;

import java.math.BigDecimal;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;

public class Rider {
    private static AtomicInteger nextId = new AtomicInteger(1);
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
        if(name.isBlank()){
            throw new PlatformException("Name cant be empty");
        }
        if(district.isBlank()){
            throw new PlatformException("District cant be empty");
        }
        id="RD-"+nextId.getAndIncrement();
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
            throw new PlatformException("No active order.");
        }
        activeOrder=null;
    }
    public boolean canHandleOrder(Order order){
        return  isAvailable() && !hasActiveOrder() && dispatchStrategy.canAssign(order);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Rider rider = (Rider) o;
        return Objects.equals(id, rider.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
