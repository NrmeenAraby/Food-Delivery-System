package org.example;

import Consoles.*;
import Domain.AuditLog;
import Domain.Order;
import Domain.PlatformConfig;
import Repositories.*;
import Services.*;
import Utils.InputHelper;

public class Main {
    public static void main(String[] args) {
        InputHelper inputHelper =new InputHelper();
        AuditLog auditLog=new AuditLog();


        CustomerRepository customerRepository=new CustomerRepository();
        MenuItemRepository menuItemRepository=new MenuItemRepository();
        OrderRepository orderRepository=new OrderRepository();
        PromotionRepository promotionRepository=new PromotionRepository();
        RestaurantRepository restaurantRepository=new RestaurantRepository();
        RiderRepository riderRepository=new RiderRepository();


        CustomerService customerService=new CustomerService(customerRepository,orderRepository);
        MenuItemService menuItemService=new MenuItemService(menuItemRepository);
        OrderService orderService=new OrderService(orderRepository,auditLog,riderRepository,restaurantRepository);
        PromotionService promotionService=new PromotionService(promotionRepository);
        RestaurantService restaurantService=new RestaurantService(restaurantRepository,orderRepository);
        ReportService reportService=new ReportService(orderRepository,restaurantRepository,riderRepository,
                customerRepository);
        RiderService riderService=new RiderService(riderRepository,orderService,reportService);

        AdminConsole adminConsole=new AdminConsole(inputHelper,restaurantService,customerService,riderService,
                promotionService,reportService);
        CustomerConsole customerConsole=new CustomerConsole(inputHelper,customerService,restaurantService,orderService,
                promotionRepository,reportService);
        RestaurantConsole restaurantConsole=new RestaurantConsole(inputHelper,restaurantService,orderService,
                menuItemService);
        RiderConsole riderConsole=new RiderConsole(inputHelper,riderService);

        MainConsole mainConsole=new MainConsole(inputHelper,customerConsole,restaurantConsole,riderConsole,adminConsole);
        mainConsole.start();
    }
}