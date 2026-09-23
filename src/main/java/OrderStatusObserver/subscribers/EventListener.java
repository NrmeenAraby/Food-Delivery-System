package OrderStatusObserver.subscribers;

import Domain.Order;

public interface EventListener {
    void update(Order order);
}
