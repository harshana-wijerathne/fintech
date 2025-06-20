package site.wijerathne.harshana.fintech.exception.customer;

public class CustomerCreationException extends CustomerServiceException {
    public CustomerCreationException(String message) {
        super(message);
    }

    public CustomerCreationException(String message, Throwable cause) {
        super(message, cause);
    }
}
