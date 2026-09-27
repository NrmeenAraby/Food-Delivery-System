package org.example;

import Consoles.*;
import Domain.AuditLog;
import Domain.RiderDashBoard;
import Repositories.*;
import Services.*;
import Utils.InputHelper;

public class Main {
    public static void main(String[] args) {
        InputHelper inputHelper =new InputHelper();
        AuditLog auditLog=new AuditLog();

        //Repositories
        CustomerRepository customerRepository=new CustomerRepository();
        MenuItemRepository menuItemRepository=new MenuItemRepository();
        OrderRepository orderRepository=new OrderRepository();
        PromotionRepository promotionRepository=new PromotionRepository();
        RestaurantRepository restaurantRepository=new RestaurantRepository();
        RiderRepository riderRepository=new RiderRepository();
        RiderDashBoard riderDashBoard=new RiderDashBoard();

        //services
        CustomerService customerService=new CustomerService(customerRepository,orderRepository);
        MenuItemService menuItemService=new MenuItemService(menuItemRepository);
        OrderService orderService=new OrderService(orderRepository,auditLog,riderRepository,restaurantRepository,riderDashBoard);
        PromotionService promotionService=new PromotionService(promotionRepository);
        RestaurantService restaurantService=new RestaurantService(restaurantRepository,orderRepository);
        ReportService reportService=new ReportService(orderRepository,restaurantRepository,riderRepository,
                customerRepository);
        RiderService riderService=new RiderService(riderRepository,orderService,reportService);

        //consoles
        AdminConsole adminConsole=new AdminConsole(inputHelper,restaurantService,customerService,riderService,
                promotionService,reportService, auditLog);
        CustomerConsole customerConsole=new CustomerConsole(inputHelper,customerService,restaurantService,orderService,
                promotionRepository,reportService);
        RestaurantConsole restaurantConsole=new RestaurantConsole(inputHelper,restaurantService,orderService,
                menuItemService);
        RiderConsole riderConsole=new RiderConsole(inputHelper,riderService);

        //main console
        MainConsole mainConsole=new MainConsole(inputHelper,customerConsole,restaurantConsole,riderConsole,adminConsole);
        mainConsole.start();
    }
}