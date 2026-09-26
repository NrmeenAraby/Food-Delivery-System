package Domain;

import Exceptions.PlatformException;

import java.math.BigDecimal;

public class OrderLine {
    private MenuItem menuItem;
    private BigDecimal quantity;

    public OrderLine(MenuItem menuItem, BigDecimal quantity) {
        if (menuItem == null) {
            throw new PlatformException("Menu item cannot be null");
        }

        if (quantity == null || quantity.compareTo(BigDecimal.ZERO) <= 0) {
            throw new PlatformException("Quantity must be greater than zero");
        }
        this.menuItem = menuItem;
        this.quantity=quantity;
    }

    public MenuItem getMenuItem() {
        return menuItem;
    }

    public BigDecimal getQuantity() {
        return quantity;
    }
    public BigDecimal calculateOrderLine(){
        return menuItem.calculateItemPrice().multiply(quantity);
    }

}
