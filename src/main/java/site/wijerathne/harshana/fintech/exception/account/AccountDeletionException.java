package site.wijerathne.harshana.fintech.exception.account;

public class AccountDeletionException extends AccountServiceException {
    public AccountDeletionException(String message) {
        super(message);
    }

    public AccountDeletionException(String message, Throwable cause) {
        super(message, cause);
    }
}
