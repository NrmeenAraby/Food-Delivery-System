package Factories;

import Domain.*;

import java.math.BigDecimal;
import java.util.List;

public class MenuItemFactory {
    public MenuItem createMenuItem(MenuItemType menuItemType, String name, ItemCategory itemCategory, int preparationTimeMinutes, double stockQuantity
    , BigDecimal price, BigDecimal discount, List<MenuItem>comboItems){
        return switch (menuItemType){
            case STANDARD -> new StandardItem(name,itemCategory,preparationTimeMinutes,stockQuantity,price);
            case WEIGHTED -> new WeightedItem(name,itemCategory,preparationTimeMinutes,stockQuantity,price);
            case COMBO    -> {
                ComboItem comboItem=new ComboItem(name, itemCategory, preparationTimeMinutes, stockQuantity, discount);
                if (comboItems != null) {
                    comboItems.forEach(comboItem::addItem);
                }
                yield  comboItem;
            }
        };
    }
}
