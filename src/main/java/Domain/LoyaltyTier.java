package Domain;

import java.math.BigDecimal;

public enum LoyaltyTier {
    Bronze(BigDecimal.ZERO),
    Silver(BigDecimal.valueOf(0.1)),
    Gold(BigDecimal.valueOf(1));
    private BigDecimal discount;
    LoyaltyTier(BigDecimal discount){
        this.discount=discount;
    }
}
