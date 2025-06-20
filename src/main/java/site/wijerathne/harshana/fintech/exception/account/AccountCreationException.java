package site.wijerathne.harshana.fintech.exception.account;

public class AccountCreationException extends AccountServiceException {
    public AccountCreationException(String message) {
        super(message);
    }

    public AccountCreationException(String message, Throwable cause) {
        super(message, cause);
    }
}
