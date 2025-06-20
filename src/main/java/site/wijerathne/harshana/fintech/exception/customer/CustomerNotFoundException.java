package site.wijerathne.harshana.fintech.exception.customer;

public class CustomerNotFoundException extends CustomerServiceException {
    public CustomerNotFoundException(String customerId) {
        super("Customer not found with ID: " + customerId);
    }
}
