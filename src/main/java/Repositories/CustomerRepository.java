package Repositories;

import Domain.Customer;
import Domain.Order;
import Domain.Restaurant;

import java.util.HashMap;
import java.util.List;

public class CustomerRepository {
    private HashMap<String, Customer> customers=new HashMap<>();
    public void addCustomer(Customer customer){
        customers.put(customer.getId(),customer);
    }
    public Customer findById(String customerId){
        return customers.get(customerId);
    }
    public List<Customer> getAllCustomers(){
        return customers.values().stream().toList();
    }

}
