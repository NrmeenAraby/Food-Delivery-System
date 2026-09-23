package OrderStatusObserver;

import java.util.ArrayList;

import Domain.Order;
import OrderStatusObserver.subscribers.EventListener;
import java.util.List;

public class EventPublisher {
    private final List<EventListener> listeners;
    public EventPublisher(){
        listeners=new ArrayList<>();
    }
    public void subscribe(EventListener eventListener){
        listeners.add(eventListener);
    }
    public void unsubscribe(EventListener eventListener){
        listeners.remove(eventListener);
    }
    public void notifyListeners(Order order){
        listeners.forEach(eventListener -> eventListener.update(order));
    }
}
