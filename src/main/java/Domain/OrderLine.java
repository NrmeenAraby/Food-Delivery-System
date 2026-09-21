package Domain;

import java.math.BigDecimal;

public record OrderLine(MenuItem item, BigDecimal quantity) {
    public OrderLine{
        if(item==null){
            throw  new IllegalArgumentException("Item cant be null");
        }
        if(quantity==null || quantity.compareTo(BigDecimal.ZERO)<=0){
            throw new IllegalArgumentException("Quantity must be greater than zero");
        }
    }
}
