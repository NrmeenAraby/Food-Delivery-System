package Domain;

import java.math.BigDecimal;

public class StandardItem extends MenuItem{
    private BigDecimal price;

    public StandardItem(String name, ItemCategory itemCategory, int preparationTimeMinutes,double stockQuantity,BigDecimal price) {
        super(name,itemCategory, preparationTimeMinutes,stockQuantity);
        this.price=price;
    }

    @Override
    public BigDecimal calculateItemPrice() {
        return price;
    }
}
