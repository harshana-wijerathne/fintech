package site.wijerathne.harshana.fintech.service.account;

import org.modelmapper.ModelMapper;
import site.wijerathne.harshana.fintech.dto.account.AccountDetailsResponseDTO;
import site.wijerathne.harshana.fintech.dto.account.AccountRequestDTO;
import site.wijerathne.harshana.fintech.exception.DataAccessException;
import site.wijerathne.harshana.fintech.model.Account;
import site.wijerathne.harshana.fintech.model.AccountDetails;
import site.wijerathne.harshana.fintech.repo.account.AccountRepo;
import site.wijerathne.harshana.fintech.util.Page;

import javax.servlet.http.HttpServletResponse;
import java.sql.Connection;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class AccountServiceImpl implements AccountService {
    private static final Logger logger = Logger.getLogger(AccountServiceImpl.class.getName());
    AccountRepo accountRepo;

    public AccountServiceImpl(AccountRepo accountRepo) {
        this.accountRepo = accountRepo;
    }

    @Override
    public AccountDetailsResponseDTO getAccountById(String accountNumber) {
        return null;
    }

    @Override
    public Page<AccountDetailsResponseDTO> getAllAccounts(String PageReq , String PageSizeReq) {
        int page = 1;
        int pageSize = 8;

        try {
            if (PageReq != null) {
                page = Integer.parseInt(PageReq);
            }
            if (PageSizeReq != null) {
                pageSize = Integer.parseInt(PageSizeReq);
            }
        } catch (NumberFormatException e) {
            throw new NumberFormatException();
        }

        try{
            Page<AccountDetails> allAccountsDetails = accountRepo.getAllAccountsDetails(page, pageSize);
            ModelMapper mapper = new ModelMapper();
            List<AccountDetailsResponseDTO> accountDetailsResponseDTOList =
                    allAccountsDetails.getContent().stream()
                    .map(account -> mapper.map(account, AccountDetailsResponseDTO.class)).collect(Collectors.toList());
            return new Page<AccountDetailsResponseDTO>(
                    accountDetailsResponseDTOList,
                    allAccountsDetails.getCurrentPage(),
                    allAccountsDetails.getPageSize(),
                    allAccountsDetails.getTotalRecords(),
                    allAccountsDetails.getTotalPages()
            );

        }catch (Exception e){
            throw new RuntimeException(e);
        }
    }

    @Override
    public AccountDetailsResponseDTO saveAccount(AccountRequestDTO accountRequestDTO) {
        return null;
    }

    @Override
    public AccountDetailsResponseDTO updateAccountDetails(AccountDetailsResponseDTO accountDetails) {
        return null;
    }

    @Override
    public boolean deleteAccountById(String accountNumber) {
        return false;
    }

    @Override
    public List<AccountDetailsResponseDTO> searchAccount(String accountNumber) {
        return List.of();
    }
}


