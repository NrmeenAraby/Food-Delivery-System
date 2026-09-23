package OrderStatusObserver.subscribers;

import Domain.Order;
import Domain.OrderStatus;
import Domain.PlatformConfig;

public class RiderDashboardListener implements EventListener{
    private final PlatformConfig config = PlatformConfig.getInstance();
    @Override
    public void update(Order order) {
           if(order.getOrderStatus()== OrderStatus.ASSIGNED){
               config.addRiderDashboardEntry(  "Order " + order.getId()
                       + " assigned to rider " + order.getRiderId()
                       + " at " + order.getAssignedAt());
           }
    }
}
