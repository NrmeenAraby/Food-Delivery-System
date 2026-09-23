package OrderStatusObserver.subscribers;

import Domain.Order;

public class CustomerNotificationListener implements EventListener{
    @Override
    public void update(Order order) {
        switch(order.getOrderStatus()){
            case ACCEPTED -> System.out.println("Your order has been accepted.");
            case PREPARING -> System.out.println("Your order is being prepared.");
            case READY -> System.out.println("Your order is ready.");
            case OUT_FOR_DELIVERY -> System.out.println("Your order is out for delivery.");
            case DELIVERED -> System.out.println("Your order has been delivered.");
            case CANCELLED -> System.out.println("Your order has been cancelled.");
        }
    }
}
