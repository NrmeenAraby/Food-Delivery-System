package PromotionStrategies;

import java.math.BigDecimal;

public class PercentagePromotionStrategy implements PromotionStrategy{
    private final BigDecimal PERCENTAGE =BigDecimal.valueOf(0.15);
    private final BigDecimal MAX_DISCOUNT_CAP=BigDecimal.valueOf(50);
    @Override
    public BigDecimal calculatePromotionDiscount(BigDecimal subTotal, BigDecimal deliveryFee) {
        BigDecimal discount=subTotal.multiply(PERCENTAGE);
        return discount.min(MAX_DISCOUNT_CAP);
    }
}
