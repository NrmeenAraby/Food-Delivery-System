package OrderStatusObserver.subscribers;

import Domain.Order;
import Domain.OrderStatus;
import Domain.PlatformConfig;
import Repositories.RiderRepository;

import java.time.LocalDateTime;

public class RiderDashboardListener implements EventListener{
    private final PlatformConfig config ;
    private final RiderRepository riderRepository;
    public RiderDashboardListener(PlatformConfig config, RiderRepository riderRepository) {
        this.config = config;
        this.riderRepository = riderRepository;
    }

    @Override
    public void update(Order order) {
           if(order.getOrderStatus()== OrderStatus.ASSIGNED){
               config.addRiderDashboardEntry(  "Order " + order.getId()
                       + " assigned to rider " + order.getRiderId()
                       + " at " + order.getAssignedAt());
           }
           if(order.getOrderStatus()==OrderStatus.CANCELLED&&order.getRiderId()!=null){
               config.addRiderDashboardEntry(  "Order " + order.getId()
                       + " with rider: " + order.getRiderId()
                       +" cancelled at: "+ LocalDateTime.now());
                riderRepository.findById(order.getRiderId()).cancelOrder();

           }
    }
}
