package OrderStatusObserver.subscribers;

import Domain.Order;
import Domain.OrderStatus;
import Domain.RiderDashBoard;
import Repositories.RiderRepository;

import java.time.LocalDateTime;

public class RiderDashboardListener implements EventListener{
    private final RiderRepository riderRepository;
    private final RiderDashBoard riderDashBoard;
    public RiderDashboardListener(RiderRepository riderRepository, RiderDashBoard riderDashBoard) {
        this.riderRepository = riderRepository;
        this.riderDashBoard = riderDashBoard;
    }

    @Override
    public void update(Order order) {
           if(order.getOrderStatus()== OrderStatus.ASSIGNED){
               riderDashBoard.addEntry(  "Order " + order.getId()
                       + " assigned to rider " + order.getRiderId()
                       + " at " + order.getAssignedAt());
           }
           if(order.getOrderStatus()==OrderStatus.CANCELLED&&order.getRiderId()!=null){
               riderDashBoard.addEntry(  "Order " + order.getId()
                       + " with rider: " + order.getRiderId()
                       +" cancelled at: "+ LocalDateTime.now());
                riderRepository.findById(order.getRiderId()).cancelOrder();

           }
    }
}
