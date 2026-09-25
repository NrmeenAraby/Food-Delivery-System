package PromotionStrategies;

import java.math.BigDecimal;

public class PercentagePromotionStrategy implements PromotionStrategy{
    private final BigDecimal PERCENTAGE;
    private final BigDecimal MAX_DISCOUNT_CAP;

    public PercentagePromotionStrategy(BigDecimal PERCENTAGE, BigDecimal MAX_DISCOUNT_CAP) {
        this.PERCENTAGE = PERCENTAGE;
        this.MAX_DISCOUNT_CAP = MAX_DISCOUNT_CAP;
    }

    @Override
    public BigDecimal calculatePromotionDiscount(BigDecimal subTotal, BigDecimal deliveryFee) {
        BigDecimal discount=subTotal.multiply(PERCENTAGE);
        return discount.min(MAX_DISCOUNT_CAP);
    }
}
