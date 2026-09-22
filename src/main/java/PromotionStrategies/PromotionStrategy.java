package PromotionStrategies;

import java.math.BigDecimal;

public interface PromotionStrategy {
    BigDecimal calculatePromotionDiscount(BigDecimal subTotal, BigDecimal deliveryFee);
}
