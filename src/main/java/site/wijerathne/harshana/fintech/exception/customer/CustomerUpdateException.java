package site.wijerathne.harshana.fintech.exception.customer;

public class CustomerUpdateException extends CustomerServiceException {
    public CustomerUpdateException(String message) {
        super(message);
    }

    public CustomerUpdateException(String message, Throwable cause) {
        super(message, cause);
    }
}
