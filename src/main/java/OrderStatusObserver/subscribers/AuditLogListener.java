package OrderStatusObserver.subscribers;

import Domain.AuditLog;
import Domain.Order;

import java.time.LocalDateTime;

public class AuditLogListener implements EventListener{
    private final AuditLog auditLog;

    public AuditLogListener(AuditLog auditLog) {
        this.auditLog = auditLog;
    }
    @Override
    public void update(Order order) {
        auditLog.addEntry(
                "Order " + order.getId()
                        + " changed to " + order.getOrderStatus()
                        + " at " + LocalDateTime.now()
        );
    }
}
