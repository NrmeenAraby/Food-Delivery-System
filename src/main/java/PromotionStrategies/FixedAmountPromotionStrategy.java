package PromotionStrategies;

import java.math.BigDecimal;

public class FixedAmountPromotionStrategy implements PromotionStrategy{
    private final BigDecimal FIXED_AMOUNT= BigDecimal.valueOf(60);
    @Override
    public BigDecimal calculatePromotionDiscount(BigDecimal subTotal, BigDecimal deliveryFee) {
        return FIXED_AMOUNT.min(subTotal);
    }

}
