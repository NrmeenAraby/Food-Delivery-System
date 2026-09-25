package Services;

import Domain.Customer;
import Domain.SearchCriteria;
import Repositories.CustomerRepository;

public class CustomerService {
    private final CustomerRepository customerRepository;
    public CustomerService(CustomerRepository customerRepository){
        this.customerRepository=customerRepository;
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
