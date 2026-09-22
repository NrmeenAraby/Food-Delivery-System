package Repositories;

import Domain.Customer;

import java.util.HashMap;

public class CustomerRepository {
    private HashMap<String, Customer> customers=new HashMap<>();
    public Customer findById(String customerId){
        return customers.get(customerId);
    }

}
