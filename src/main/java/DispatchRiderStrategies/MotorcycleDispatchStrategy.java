package DispatchRiderStrategies;

import Domain.Order;

import java.math.BigDecimal;

public class MotorcycleDispatchStrategy implements DispatchStrategy{
    private final BigDecimal maxRange;
    private final BigDecimal maxOrderUnits;
    private final BigDecimal maxSpeed;
    public MotorcycleDispatchStrategy(BigDecimal maxRange,BigDecimal maxOrderUnits,BigDecimal maxSpeed){
        this.maxRange=maxRange;
        this.maxOrderUnits=maxOrderUnits;
        this.maxSpeed=maxSpeed;
    }
    @Override
    public boolean canAssign(Order order) {
        return order.getDistanceKm().compareTo(maxRange)<=0 && order.getTotalOrderUnits().compareTo(maxOrderUnits)<=0;
    }

    @Override
    public BigDecimal getMaxSpeedKmPerHour() {
        return maxSpeed;
    }
}
