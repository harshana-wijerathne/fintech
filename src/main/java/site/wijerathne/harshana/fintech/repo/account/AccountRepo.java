package site.wijerathne.harshana.fintech.repo.account;

import site.wijerathne.harshana.fintech.dto.account.AccountDetailsResponseDTO;
import site.wijerathne.harshana.fintech.model.Account;
import site.wijerathne.harshana.fintech.model.AccountDetails;
import site.wijerathne.harshana.fintech.util.Page;

import java.util.List;
import java.util.Optional;

public interface AccountRepo {
    Optional<Account> getAccountById(String accountNumber);
    List<Account> getAllAccounts();
    Account saveAccount(Account account);
    Account updateAccountDetails(Account account);
    boolean deleteAccount(String accountNumber);
    List<AccountDetails> searchAccounts(String search);
    public Optional<AccountDetails> getAccountDetailsById(String accountNumber);
    public Page<AccountDetails> getAllAccountsDetails(int page, int size);



}
