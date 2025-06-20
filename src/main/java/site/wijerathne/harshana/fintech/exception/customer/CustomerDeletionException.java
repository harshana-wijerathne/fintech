package site.wijerathne.harshana.fintech.exception.customer;

public class CustomerDeletionException extends CustomerServiceException {
    public CustomerDeletionException(String message) {
        super(message);
    }

    public CustomerDeletionException(String message, Throwable cause) {
        super(message, cause);
    }
}
