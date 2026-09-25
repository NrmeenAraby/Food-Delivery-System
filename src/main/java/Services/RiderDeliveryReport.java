package Services;

import Domain.Order;

import java.time.Duration;
import java.util.List;

public record RiderDeliveryReport(String riderId, List<Order> deliveredOrders, Duration avgDeliveryDuration) {
    public int getOrdersSize(){
        return deliveredOrders.size();
    }
}
