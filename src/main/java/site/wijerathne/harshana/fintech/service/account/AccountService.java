package site.wijerathne.harshana.fintech.service.account;

import site.wijerathne.harshana.fintech.dto.account.AccountDetailsRequestDTO;
import site.wijerathne.harshana.fintech.dto.account.AccountDetailsResponseDTO;
import site.wijerathne.harshana.fintech.dto.account.AccountRequestDTO;
import site.wijerathne.harshana.fintech.util.Page;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

public interface AccountService {
    AccountDetailsResponseDTO getAccountByAccountNumber(String accountNumber);
    Page<AccountDetailsResponseDTO> getAllAccounts(String Page , String PageSize);
    AccountDetailsResponseDTO saveAccount(AccountRequestDTO accountRequestDTO,HttpServletRequest request);
    AccountDetailsResponseDTO updateAccountDetails(AccountDetailsRequestDTO accountDetails , HttpServletRequest request);
    boolean deleteAccountById(String accountNumber,HttpServletRequest request);
    List<AccountDetailsResponseDTO> searchAccount(String accountNumber);

}

