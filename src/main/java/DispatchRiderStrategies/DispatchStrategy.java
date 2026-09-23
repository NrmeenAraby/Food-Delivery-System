package DispatchRiderStrategies;

import Domain.Order;

import java.math.BigDecimal;

public interface DispatchStrategy {
    boolean canAssign(Order order);
    BigDecimal getMaxSpeedKmPerHour();
}
