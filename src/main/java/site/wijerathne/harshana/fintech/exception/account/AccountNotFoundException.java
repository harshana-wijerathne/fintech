package site.wijerathne.harshana.fintech.exception.account;

public class AccountNotFoundException extends AccountServiceException {
    public AccountNotFoundException(String accountNumber) {
        super("Account not found with number: " + accountNumber);
    }
}
