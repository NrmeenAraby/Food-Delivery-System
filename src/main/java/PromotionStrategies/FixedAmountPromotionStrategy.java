package PromotionStrategies;

import java.math.BigDecimal;

public class FixedAmountPromotionStrategy implements PromotionStrategy{
    private final BigDecimal FIXED_AMOUNT;

    public FixedAmountPromotionStrategy(BigDecimal FIXED_AMOUNT) {
        this.FIXED_AMOUNT = FIXED_AMOUNT;
    }

    @Override
    public BigDecimal calculatePromotionDiscount(BigDecimal subTotal, BigDecimal deliveryFee) {
        return FIXED_AMOUNT.min(subTotal);
    }

}
