package Consoles;

import Domain.*;
import Exceptions.PlatformException;
import Services.MenuItemService;
import Services.OrderService;
import Services.RestaurantService;
import Utils.InputHelper;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class RestaurantConsole {
    private String restaurantId;
    private final InputHelper inputHelper;
    private final RestaurantService restaurantService;
    private final OrderService orderService;
    private final MenuItemService menuItemService;

    public RestaurantConsole(InputHelper inputHelper, RestaurantService restaurantService, OrderService orderService, MenuItemService menuItemService) {
        this.inputHelper = inputHelper;
        this.restaurantService = restaurantService;
        this.orderService = orderService;
        this.menuItemService = menuItemService;
    }

    public void start() {
        this.restaurantId=inputHelper.readString("Restaurant ID: ");

        int choice;
        do {
            if(restaurantService.findById(restaurantId)==null){
                throw new PlatformException("No restaurant with this ID");
            }
            showMenu();
            choice = inputHelper.readInt("Choose an option: ");
            try{
                switch (choice) {
                    case 1 -> viewPendingOrders();
                    case 2 -> acceptPendingOrder();
                    case 3 -> rejectPendingOrder();
                    case 4 -> viewOrdersNeedToBePrepared();
                    case 5 -> markPreparing();
                    case 6 -> viewOrdersCanBeReady();
                    case 7 -> markReady();
                    case 8 -> toggleItemAvailability();
                    case 9 -> addMenuItem();
                    case 10 -> removeMenuItem();
                    case 11 -> adjustItemStock();
                    case 12 -> viewTodayOrdersAndRevenue();
                    case 0 -> System.out.println("Ciao!");
                    default -> System.out.println("Invalid choice. Please try again.");
                }
            }catch(PlatformException e){
                System.out.println(e.getMessage());
            }
        }while(choice!=0);
    }
    private void viewTodayOrdersAndRevenue(){
       List<Order>orders= orderService.viewTodayOrders(restaurantId);
       BigDecimal revenue=orderService.viewTodayRevenue(restaurantId);
        System.out.println("\n===== Today's Orders =====");

        if (orders.isEmpty()) {
            System.out.println("No orders today.");
        } else {
            for (Order order : orders) {
                System.out.println(
                        order.getId() + " | Customer: " + order.getCustomer().getName() +
                                " | Status: " + order.getOrderStatus()
                );
            }
        }
        System.out.println("Today's Revenue: " + revenue + " EGP");
    }
    private void removeMenuItem(){
        String menuItemId=inputHelper.readString("Enter the Item ID: ");
        Restaurant restaurant=restaurantService.findById(restaurantId);
        menuItemService.removeMenuItem(menuItemId,restaurant);
        System.out.println("Removed successfully");
    }
    private void addMenuItem(){
        System.out.println("Enter the item info");
        MenuItemType menuItemType=readMenuItemType();
        String name=inputHelper.readString("Name: ");
        ItemCategory itemCategory=readItemCategory();
        int preparationTimeMinutes;
        do{
            preparationTimeMinutes=inputHelper.readInt("Preparation time in minutes: ");
            if(preparationTimeMinutes<=1 ||preparationTimeMinutes>120){
                System.out.println("Be Sensible (must be between 2 to 120 minutes)");
            }
        }while (preparationTimeMinutes<=1 ||preparationTimeMinutes>120);
        double stockQuantity;
        do {
            stockQuantity = inputHelper.readDouble("Stock Quantity: ");
            if(stockQuantity<=0) {
                System.out.println("Enter positive number");
            }
        }while (stockQuantity<=0);
        BigDecimal price=BigDecimal.ZERO;
        BigDecimal discount=BigDecimal.ZERO;
        List<MenuItem>comboItems=new ArrayList<>();
        if(menuItemType!=MenuItemType.COMBO){
            do {
                price = inputHelper.readBigDecimal("Price: ");

                if (price.compareTo(BigDecimal.ZERO) <= 0) {
                    System.out.println("Price must be greater than zero.");
                }
            } while (price.compareTo(BigDecimal.ZERO) <= 0);
        }
        else{
            do {
                discount = inputHelper.readBigDecimal("Discount (e.g. 0.20 for 20%): ");
                if (discount.compareTo(BigDecimal.ZERO) < 0 ||
                        discount.compareTo(BigDecimal.ONE) > 0) {
                    System.out.println("Discount must be between 0 and 1.");
                }
            } while (discount.compareTo(BigDecimal.ZERO) < 0 || discount.compareTo(BigDecimal.ONE) > 0);
            comboItems = readComboItems();
        }
        Restaurant restaurant=restaurantService.findById(restaurantId);
        menuItemService.addMenuItem(restaurant,menuItemType,name,itemCategory,preparationTimeMinutes,
                stockQuantity,price,discount,comboItems);
        System.out.println("Added successfully");
    }

    private void adjustItemStock(){
        String menuItemId=inputHelper.readString("Enter the Item ID: ");
        double newStock=inputHelper.readDouble("Enter the new stock: ");
        Restaurant restaurant=restaurantService.findById(restaurantId);
        menuItemService.adjustItemStock(menuItemId,newStock,restaurant);
        System.out.println("Updated Successfully, current stock is: "+newStock);
    }
    private void toggleItemAvailability(){
        String menuItemId=inputHelper.readString("Enter the Item ID: ");
        Restaurant restaurant=restaurantService.findById(restaurantId);
        boolean isAvailable=menuItemService.toggleItemAvailability(menuItemId,restaurant);
        System.out.println("Toggled successfully, now it is "+(isAvailable?"Available":"Unavailable"));
    }
    private void markReady(){
        String orderId=inputHelper.readString("Enter the order ID: ");
        orderService.markReady(orderId,restaurantId);
        System.out.println("Marked as Ready");
    }
    private void markPreparing(){
        String orderId=inputHelper.readString("Enter the order ID: ");
        orderService.markPreparing(orderId,restaurantId);
        System.out.println("Marked as Preparing");
    }
    private void viewOrdersCanBeReady(){
        viewOrdersWithSpecificStatus(OrderStatus.PREPARING);
    }
    private void viewOrdersNeedToBePrepared(){
        viewOrdersWithSpecificStatus(OrderStatus.ACCEPTED);
    }
    private void rejectPendingOrder(){
        String orderId=inputHelper.readString("Enter the order ID: ");
        orderService.rejectPendingOrder(orderId,restaurantId);
        System.out.println("Rejected successfully");
    }
    private void acceptPendingOrder(){
        String orderId=inputHelper.readString("Enter the order ID: ");
        orderService.acceptPendingOrder(orderId,restaurantId);
        System.out.println("Accepted successfully");
    }
    private void viewPendingOrders(){
        viewOrdersWithSpecificStatus(OrderStatus.PLACED);
    }
    private void viewOrdersWithSpecificStatus(OrderStatus orderStatus){
       List<Order> orders= restaurantService.getOrdersWithSpecificStatus(restaurantId,orderStatus);
       if(orders.isEmpty()){
           System.out.println("No orders with status " + orderStatus + ".");           return;
       }
       for(int i=0;i<orders.size();i++){
           Order order=orders.get(i);
           System.out.println((i+1)+". "+order.getId()+" | "+"Customer: "+order.getCustomer().getName());
       }
    }
    private List<MenuItem> readComboItems() {
        List<MenuItem> menuItems = restaurantService.getMenu(restaurantId).stream()
                .filter(item -> !(item instanceof ComboItem))
                .toList();

        if (menuItems.isEmpty()) {
            throw new PlatformException(
                    "There are no items available to add to a combo."
            );
        }
        List<MenuItem> selectedItems = new ArrayList<>();
        while (true) {
            System.out.println("\nChoose items for the combo:");
            for (int i = 0; i < menuItems.size(); i++) {
                System.out.println(
                        (i + 1) + ". " + menuItems.get(i).getName()
                );
            }
            System.out.println("0. Done");
            int choice = inputHelper.readInt("Choose item: ");
            if (choice == 0) {
                break;
            }
            if (choice < 1 || choice > menuItems.size()) {
                System.out.println("Invalid choice.");
                continue;
            }
            MenuItem selectedItem = menuItems.get(choice - 1);
            if (selectedItems.contains(selectedItem)) {
                System.out.println("This item is already in the combo.");
                continue;
            }
            selectedItems.add(selectedItem);
            System.out.println(
                    selectedItem.getName() + " added to combo."
            );
        }
        if (selectedItems.isEmpty()) {
            throw new PlatformException(
                    "A combo must contain at least one item."
            );
        }
        return selectedItems;
    }
    private ItemCategory readItemCategory() {
        System.out.println("Choose Item Category:");

        for (int i = 0; i < ItemCategory.values().length; i++) {
            System.out.println((i + 1) + ". " + ItemCategory.values()[i]);
        }

        int choice;
        do {
            choice = inputHelper.readInt("Enter your choice: ");

            if (choice < 1 || choice > ItemCategory.values().length) {
                System.out.println("Invalid choice. Try again.");
            }
        } while (choice < 1 || choice > ItemCategory.values().length);

        return ItemCategory.values()[choice - 1];
    }
    private MenuItemType readMenuItemType() {
        System.out.println("Menu Item Type:");

        for (int i = 0; i < MenuItemType.values().length; i++) {
            System.out.println((i + 1) + ". " + MenuItemType.values()[i]);
        }

        int choice;
        do {
            choice = inputHelper.readInt("Enter your choice: ");

            if (choice < 1 || choice > MenuItemType.values().length) {
                System.out.println("Invalid choice. Try again.");
            }
        } while (choice < 1 || choice > MenuItemType.values().length);

        return MenuItemType.values()[choice - 1];
    }
    private void showMenu() {
        System.out.println("\n===== Restaurant Console =====");
        System.out.println("1. View Pending Orders");
        System.out.println("2. Accept Order");
        System.out.println("3. Reject Order");
        System.out.println("4. View Orders to Prepare");
        System.out.println("5. Mark Order as Preparing");
        System.out.println("6. View Orders to Mark Ready");
        System.out.println("7. Mark Order as Ready");
        System.out.println("8. Toggle Item Availability");
        System.out.println("9. Add Menu Item");
        System.out.println("10. Remove Menu Item");
        System.out.println("11. Adjust Item Stock");
        System.out.println("12. View Today's Orders & Revenue");
        System.out.println("0. Back");
    }
}
