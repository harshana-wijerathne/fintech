package site.wijerathne.harshana.fintech.service.account;

import site.wijerathne.harshana.fintech.dto.account.AccountDetailsResponseDTO;
import site.wijerathne.harshana.fintech.dto.account.AccountRequestDTO;
import site.wijerathne.harshana.fintech.util.Page;

import java.util.List;

public interface AccountService {
    AccountDetailsResponseDTO getAccountByAccountNumber(String accountNumber);
    Page<AccountDetailsResponseDTO> getAllAccounts(String Page , String PageSize);
    AccountDetailsResponseDTO saveAccount(AccountRequestDTO accountRequestDTO);
    AccountDetailsResponseDTO updateAccountDetails(AccountDetailsResponseDTO accountDetails);
    boolean deleteAccountById(String accountNumber);
    List<AccountDetailsResponseDTO> searchAccount(String accountNumber);

}

