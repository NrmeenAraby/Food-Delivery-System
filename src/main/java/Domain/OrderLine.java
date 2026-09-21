package Domain;

import java.math.BigDecimal;

public class OrderLine {
    private MenuItem menuItem;
    private BigDecimal quantity;

    public OrderLine(MenuItem menuItem, BigDecimal quantity) {
        this.menuItem = menuItem;
        this.quantity=quantity;
    }

    public MenuItem getMenuItem() {
        return menuItem;
    }

    public BigDecimal getQuantity() {
        return quantity;
    }
    public BigDecimal calculateSubTotal(){
        return menuItem.calculateItemPrice().multiply(quantity);
    }

}
