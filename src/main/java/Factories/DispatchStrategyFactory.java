package Factories;

import DispatchRiderStrategies.BicycleDispatchStrategy;
import DispatchRiderStrategies.CarDispatchStrategy;
import DispatchRiderStrategies.DispatchStrategy;
import DispatchRiderStrategies.MotorcycleDispatchStrategy;
import Domain.VehicleType;

import java.math.BigDecimal;

public class DispatchStrategyFactory {
    public static DispatchStrategy createDispatchStrategy(VehicleType vehicleType, BigDecimal maxRange, BigDecimal maxOrdersUnits, BigDecimal maxSpeed){
        return switch (vehicleType){
            case MOTORCYCLE -> new MotorcycleDispatchStrategy(maxRange,maxOrdersUnits,maxSpeed);
            case BICYCLE -> new BicycleDispatchStrategy(maxRange,maxOrdersUnits,maxSpeed);
            case CAR -> new CarDispatchStrategy(maxRange,maxOrdersUnits,maxSpeed);
        };
    }
}
