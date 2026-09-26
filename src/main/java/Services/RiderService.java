package Services;

import Domain.*;
import Exceptions.PlatformException;
import Exceptions.RiderBusyException;
import Repositories.RiderRepository;

import java.math.BigDecimal;

public class RiderService {
    private final RiderRepository riderRepository;
    private final OrderService orderService;
    private final ReportService reportService;

    public RiderService(RiderRepository riderRepository, OrderService orderService, ReportService reportService) {
        this.riderRepository = riderRepository;
        this.orderService = orderService;
        this.reportService = reportService;
    }

    public void removeRider(String riderId){
        Rider rider=riderRepository.findById(riderId);
        if(rider==null){
            throw new PlatformException("No rider with this ID");
        }
        if(rider.hasActiveOrder()){
            throw new PlatformException("Cannot remove a rider with an active order.");
        }
        riderRepository.removeRider(riderId);
    }
    public void addRider(String name, VehicleType vehicleType, String district,
             BigDecimal maxRange, BigDecimal maxOrdersUnits, BigDecimal maxSpeed){
        if(maxRange.compareTo(BigDecimal.ZERO)<=0){
            throw new PlatformException("Max range must be positive");
        }
        if(maxOrdersUnits.compareTo(BigDecimal.ZERO)<=0){
            throw new PlatformException("Max order units must be positive");
        }
        if(maxSpeed.compareTo(BigDecimal.ZERO)<=0){
            throw new PlatformException("Max speed must be positive");
        }
        Rider rider=new Rider(name,vehicleType,district,maxRange,maxOrdersUnits,maxSpeed);
        riderRepository.addRider(rider);
    }
    public RiderDeliveryReport viewStatistics(String riderId){
        getRider(riderId); // throws if no rider
        return reportService.getRiderCompleteDeliveriesAndAvgDuration().stream()
                .filter(report->report.riderId().equals(riderId))
                .findFirst()
                .orElseThrow(()->new PlatformException("No delivery statistics found for this rider."));
    }
    public void markDelivered(String riderId){
        Rider rider=getRider(riderId);
        if (!rider.hasActiveOrder()) {
            throw new PlatformException("No assigned order for rider " + riderId + ".");
        }
        Order order=rider.getActiveOrder();
        order.markDelivered();
        rider.completeDelivery();
        orderService.dispatchNextReadyOrder(rider);
    }
    public void markPickedUp(String riderId){
        Rider rider=getRider(riderId);
        if (!rider.hasActiveOrder()) {
            throw new PlatformException("No assigned order for rider " + riderId + ".");
        }
        rider.getActiveOrder().changeStatus(OrderStatus.OUT_FOR_DELIVERY);
        System.out.println("Picked Successfully");
    }
    public Order viewAssignedOrder(String riderId){
        Rider rider=getRider(riderId);
        return rider.getActiveOrder();
    }
    public void goOffDuty(String riderId){
        Rider rider=getRider(riderId);
        if(!rider.isAvailable()){
            System.out.println("Rider "+riderId +" is already off.");
            return;
        }
        if(rider.hasActiveOrder()){
            throw new RiderBusyException("Can't right now. Rider has an assigned order.");
        }
        rider.updateAvailabilityStatus(false);
        System.out.println("Rider: "+riderId+" is now off.");
    }
    public void goOnDuty(String riderId){
        Rider rider=getRider(riderId);
        if(rider.isAvailable()){
            System.out.println("Already available");
            orderService.dispatchNextReadyOrder(rider);
            return;
        }
        rider.updateAvailabilityStatus(true);
        orderService.dispatchNextReadyOrder(rider);
        System.out.println("Rider: "+riderId+" is now ready for assignment.");
    }



    public Rider getRider(String riderId){
        Rider rider=riderRepository.findById(riderId);
        if(rider==null){
            throw new PlatformException("No rider with this ID.");
        }
        return rider;
    }
    public Rider findById(String riderId){
        return  riderRepository.findById(riderId);
    }

}
