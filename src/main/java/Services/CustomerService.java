package Services;

import Domain.Customer;
import Domain.OrderStatus;
import Domain.SearchCriteria;
import Exceptions.PlatformException;
import Repositories.CustomerRepository;
import Repositories.OrderRepository;

import java.math.BigDecimal;

public class CustomerService {
    private final CustomerRepository customerRepository;
    private final OrderRepository orderRepository;
    public CustomerService(CustomerRepository customerRepository, OrderRepository orderRepository){
        this.customerRepository=customerRepository;
        this.orderRepository = orderRepository;
    }

    public void removeCustomer(String customerId){
        Customer customer=customerRepository.findById(customerId);
        if (customer == null) {
            throw new PlatformException("No customer with this ID.");
        }
        boolean haveActiveOrder = orderRepository.getAllOrders().stream()
                .anyMatch(order -> order.getCustomer().getId().equals(customerId)
                        && order.getOrderStatus()!= OrderStatus.DELIVERED
                        && order.getOrderStatus() != OrderStatus.CANCELLED);
        if(haveActiveOrder){
            throw new PlatformException( "Cannot remove customer with an active order.");
        }
        customerRepository.removeCustomer(customerId);
    }
    public void addCustomer(String name, String phoneNumber, BigDecimal walletBalance){
        Customer customer=new Customer(name,phoneNumber,walletBalance);
        customerRepository.addCustomer(customer);
    }
    public void saveSearch(String customerId, SearchCriteria searchCriteria){
        if(isThereSearchCriteria(searchCriteria)){
            Customer customer = customerRepository.findById(customerId);
            if (customer == null) {
                throw new IllegalArgumentException("Customer not found: " + customerId);
            }
            customer.addSearch(searchCriteria);
        }
    }
    private boolean isThereSearchCriteria(SearchCriteria searchCriteria){
        if(searchCriteria.getCuisineCategory()!=null)
            return true;
        if(searchCriteria.getDistrict()!=null)
            return true;
        if(searchCriteria.getMinimumRating()!=null)
            return true;
        if(searchCriteria.getPriceCeiling()!=null)
            return true;

        return false;
    }
    public Customer findById(String customerId){
        return customerRepository.findById(customerId);
    }

}
