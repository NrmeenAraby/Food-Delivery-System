package Consoles;

import Domain.Order;
import Exceptions.PlatformException;
import Services.RiderDeliveryReport;
import Services.RiderService;
import Utils.InputHelper;
public class RiderConsole {
    private final String riderId;
    private final InputHelper inputHelper;
    private final RiderService riderService;

    public RiderConsole(String riderId, InputHelper inputHelper, RiderService riderService) {
        this.riderId = riderId;
        this.inputHelper = inputHelper;
        this.riderService = riderService;
    }

    public void start() {
        int choice;
        do {
            showMenu();
            choice = inputHelper.readInt("Choose an option: ");
            try {
                switch (choice) {
                    case 1 -> goOnDuty();
                    case 2 -> goOffDuty();
                    case 3 -> viewAssignedOrder();
                    case 4 -> markPickedUp();
                    case 5 -> markDelivered();
                    case 6 -> viewStatistics();
                    case 0 -> System.out.println("Ciao!");
                    default -> System.out.println("Invalid choice. Please try again.");
                }
            } catch (PlatformException e) {
                System.out.println(e.getMessage());
            }

        } while (choice != 0);
    }
    private void viewStatistics() {
        RiderDeliveryReport report = riderService.viewStatistics(riderId);
        System.out.println("\n===== My Delivery Statistics =====");
        System.out.println("Completed Deliveries      : " + report.getOrdersSize());
        System.out.println("Average Delivery Duration: "
                + report.avgDeliveryDuration().toMinutesPart()
                + " minutes "
                + report.avgDeliveryDuration().toSecondsPart()
                + " seconds");

        System.out.println("\n===== Completed Orders =====");

        if (report.deliveredOrders().isEmpty()) {
            System.out.println("No completed orders yet.");
            return;
        }

        for (Order order : report.deliveredOrders()) {
            System.out.println(order);
        }
    }

    private void markDelivered(){
        riderService.markDelivered(riderId);
    }
    private void markPickedUp(){
        riderService.markPickedUp(riderId);
    }
    private void viewAssignedOrder(){
       Order order= riderService.viewAssignedOrder(riderId);
       if(order==null){
           System.out.println("Rider "+riderId+" has no assigned order.");
           return;
       }
        System.out.println(order);
    }
    private void goOffDuty(){
        riderService.goOffDuty(riderId);
    }
    private void goOnDuty(){
        riderService.goOnDuty(riderId);
    }
    private void showMenu() {
        System.out.println("\n===== Rider Console =====");
        System.out.println("1. Go On Duty");
        System.out.println("2. Go Off Duty");
        System.out.println("3. View Currently Assigned Order");
        System.out.println("4. Mark Order as Picked Up");
        System.out.println("5. Mark Order as Delivered");
        System.out.println("6. View Personal Delivery Statistics");
        System.out.println("0. Back");
    }
}
