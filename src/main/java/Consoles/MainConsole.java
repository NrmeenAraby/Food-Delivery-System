package Consoles;

import Utils.InputHelper;

public class MainConsole {

    private final InputHelper inputHelper;
    private final CustomerConsole customerConsole;
    private final RestaurantConsole restaurantConsole;
    private final RiderConsole riderConsole;
    private final AdminConsole adminConsole;

    public MainConsole(InputHelper inputHelper, CustomerConsole customerConsole, RestaurantConsole restaurantConsole,
                       RiderConsole riderConsole, AdminConsole adminConsole) {
        this.inputHelper = inputHelper;
        this.customerConsole = customerConsole;
        this.restaurantConsole = restaurantConsole;
        this.riderConsole = riderConsole;
        this.adminConsole = adminConsole;
    }

    public void start() {
        int choice;

        do {
            showMenu();
            choice = inputHelper.readInt("Choose an option: ");
            switch (choice) {
                case 1 -> customerConsole.start();
                case 2 -> restaurantConsole.start();
                case 3 -> riderConsole.start();
                case 4 -> adminConsole.start();
                case 0 -> System.out.println("Ciao!");
                default -> System.out.println("Invalid choice. Please try again.");
            }
        } while (choice != 0);
    }

    private void showMenu() {
        System.out.println("\n===== Masr Delivery =====");
        System.out.println("1. Customer");
        System.out.println("2. Restaurant");
        System.out.println("3. Rider");
        System.out.println("4. Admin");
        System.out.println("0. Exit");
    }
}