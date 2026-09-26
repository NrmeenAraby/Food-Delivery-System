package OrderStatusObserver.subscribers;

import Domain.Order;

public class CustomerNotificationListener implements EventListener{
    @Override
    public void update(Order order) {
        System.out.print("Customer Notifications: ");
        switch(order.getOrderStatus()){
            case ACCEPTED -> System.out.println("Order "+ order.getId()+" has been accepted.");
            case PREPARING -> System.out.println("Order "+ order.getId()+" is being prepared.");
            case READY -> System.out.println("Order "+ order.getId()+" is ready.");
            case OUT_FOR_DELIVERY -> System.out.println("Order "+ order.getId()+" is out for delivery.");
            case DELIVERED -> System.out.println("Order "+ order.getId()+" has been delivered.");
            case CANCELLED -> System.out.println("Order "+ order.getId()+" has been cancelled.");
        }
    }
}
