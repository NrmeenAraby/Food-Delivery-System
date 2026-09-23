package Domain;

import Exceptions.RiderBusyException;

public class Rider {
    private static int nextId = 1;
    private final String id;
    private String name;
    private VehicleType vehicleType;
    private String district;
    private boolean availabilityStatus;
    private int completedDeliveriesCount;
    private Order activeOrder;

    public Rider(String name, VehicleType vehicleType, String district) {
        id="RD-"+Integer.toString(nextId++);
        this.name = name;
        this.vehicleType = vehicleType;
        this.district = district;
        availabilityStatus=true;
        completedDeliveriesCount=0;
    }

    public static int getNextId() {
        return nextId;
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
        updateAvailabilityStatus(false);
    }
    public void completeDelivery(){
        if(activeOrder==null) {
            throw new IllegalStateException("No active order.");
        }
        activeOrder=null;
        updateAvailabilityStatus(true);
        incrementCompletedDeliveries();
    }
}
