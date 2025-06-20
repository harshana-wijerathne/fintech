package site.wijerathne.harshana.fintech.exception.account;

public class AccountUpdateException extends AccountServiceException {
    public AccountUpdateException(String message) {
        super(message);
    }

    public AccountUpdateException(String message, Throwable cause) {
        super(message, cause);
    }
}



