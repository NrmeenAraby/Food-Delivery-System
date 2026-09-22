package PromotionStrategies;

import java.math.BigDecimal;

public class FreeDeliveryPromotionStrategy implements PromotionStrategy{
    @Override
    public BigDecimal calculatePromotionDiscount(BigDecimal subTotal, BigDecimal deliveryFee) {
        return deliveryFee;
    }
}
