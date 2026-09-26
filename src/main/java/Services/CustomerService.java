package Services;

import Domain.Address;
import Domain.Customer;
import Domain.OrderStatus;
import Domain.SearchCriteria;
import Exceptions.PlatformException;
import Repositories.CustomerRepository;
import Repositories.OrderRepository;

import java.math.BigDecimal;
import java.util.List;

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
    public void addCustomer(String name, String phoneNumber, BigDecimal walletBalance, List<Address>addresses){
        Customer customer=new Customer(name,phoneNumber,walletBalance);
        for (Address address : addresses) {
            customer.addAddress(address);
        }
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
    private boolean isThereSearchCriteria(SearchCriteria searchCriteria) {
        return searchCriteria.getCuisineCategory() != null
                || searchCriteria.getDistrict() != null
                || searchCriteria.getMinimumRating() != null
                || searchCriteria.getPriceCeiling() != null
                || searchCriteria.getKeyword() != null;
    }
    public Customer findById(String customerId){
        return customerRepository.findById(customerId);
    }

}
