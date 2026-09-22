package Services;

import Domain.Customer;
import Repositories.CustomerRepository;

public class CustomerService {
    private final CustomerRepository customerRepository;
    public CustomerService(CustomerRepository customerRepository){
        this.customerRepository=customerRepository;
    }

}
