package site.wijerathne.harshana.fintech.exception.transaction;

public class AccountNotFoundException extends RuntimeException {
    public AccountNotFoundException(String message) {
        super(message);
    }
}
