package Services;

import Domain.*;
import Exceptions.PlatformException;
import Factories.MenuItemFactory;
import Repositories.MenuItemRepository;

import java.math.BigDecimal;
import java.util.List;

public class MenuItemService {
    private final MenuItemRepository menuItemRepository;

    public MenuItemService(MenuItemRepository menuItemRepository) {
        this.menuItemRepository = menuItemRepository;
    }
    public void removeMenuItem(String menuItemId, Restaurant restaurant){
        if(restaurant.getMenu().findById(menuItemId)==null){
            throw new PlatformException("This item doesn't belong to this restaurant.");
        }

        // Find combos that contain this item
        List<String> combosIds = restaurant.getMenu().getItems().stream()
                        .filter(item->item instanceof ComboItem)
                .map(item ->(ComboItem)item )
                .filter(comboItem -> comboItem.findById(menuItemId)!=null)
                .map(MenuItem::getId)
                .toList();

        // Remove the affected combos
        for (String comboId : combosIds) {
            restaurant.getMenu().removeItem(comboId);
            menuItemRepository.removeMenuItem(comboId);
        }

        // Remove the original item
        restaurant.getMenu().removeItem(menuItemId);
        menuItemRepository.removeMenuItem(menuItemId);
    }
    public void addMenuItem(Restaurant restaurant,MenuItemType menuItemType, String name,
                            ItemCategory itemCategory, int preparationTimeMinutes, double stockQuantity
            , BigDecimal price, BigDecimal discount, List<MenuItem> comboItems){

        MenuItem menuItem=MenuItemFactory.createMenuItem(menuItemType,name,itemCategory,preparationTimeMinutes
                ,stockQuantity,price,discount,comboItems);
        restaurant.getMenu().addItem(menuItem);
        menuItemRepository.addMenuItem(menuItem);
    }
    public void adjustItemStock(String menuItemId,double newStock,Restaurant restaurant){
        MenuItem menuItem=menuItemRepository.findById(menuItemId);
        if(menuItem==null){
            throw new PlatformException("No item with this ID");
        }
        if(restaurant.getMenu().findById(menuItemId)==null){
            throw new PlatformException("This item doesn't belong to this restaurant.");
        }
        menuItem.setStockQuantity(newStock);
    }
    public boolean toggleItemAvailability(String menuItemId, Restaurant restaurant){
        MenuItem menuItem=menuItemRepository.findById(menuItemId);
        if(menuItem==null){
            throw new PlatformException("No item with this ID");
        }
        if(! restaurant.getMenu().getItems().contains(menuItem)){
            throw new PlatformException("This item doesn't belong to this restaurant.");
        }
        menuItem.updateAvailability(!menuItem.getAvailabilityFlag());
        return menuItem.isAvailable();
    }
}
