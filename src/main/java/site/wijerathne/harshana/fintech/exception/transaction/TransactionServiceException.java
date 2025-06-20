package site.wijerathne.harshana.fintech.exception.transaction;

public class TransactionServiceException extends RuntimeException {
    public TransactionServiceException(String message,Throwable cause) {
        super(message,cause);
    }
}
