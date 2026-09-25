package Repositories;


import Domain.MenuItem;

import java.util.HashMap;
import java.util.List;

public class MenuItemRepository {
    private final HashMap<String, MenuItem> menuItems=new HashMap<>();
    public void addMenuItem(MenuItem menuItem){
        menuItems.put(menuItem.getId(),menuItem);
    }
    public MenuItem findById(String menuItemId){
        return menuItems.get(menuItemId);
    }
    public List<MenuItem> getAllMenuItems(){
        return menuItems.values().stream().toList();
    }
    public void removeMenuItem(String menuItemId){
        menuItems.remove(menuItemId);
    }
}
