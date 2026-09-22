package Domain;

import java.math.BigDecimal;

public record OrderPrice(BigDecimal subTotal, BigDecimal deliveryFee, BigDecimal serviceFee, BigDecimal promoDiscount,BigDecimal total) {
}
