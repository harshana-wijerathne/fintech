package site.wijerathne.harshana.fintech.service.account;

import org.modelmapper.ModelMapper;
import site.wijerathne.harshana.fintech.dto.account.AccountDetailsResponseDTO;
import site.wijerathne.harshana.fintech.dto.account.AccountRequestDTO;
import site.wijerathne.harshana.fintech.exception.DataAccessException;
import site.wijerathne.harshana.fintech.model.Account;
import site.wijerathne.harshana.fintech.model.AccountDetails;
import site.wijerathne.harshana.fintech.repo.account.AccountRepo;
import site.wijerathne.harshana.fintech.util.Page;

import javax.security.auth.login.AccountNotFoundException;
import javax.servlet.http.HttpServletResponse;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
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
    public AccountDetailsResponseDTO getAccountByAccountNumber(String accountNumber) {
        if(accountNumber == null) throw new IllegalArgumentException("accountNumber is null");
        Optional<AccountDetails> AccountDetails = accountRepo.getAccountDetailsById(accountNumber);
        if(AccountDetails.isEmpty()) throw new RuntimeException("account does not exist");
        ModelMapper modelMapper = new ModelMapper();
        return modelMapper.map(AccountDetails.get(), AccountDetailsResponseDTO.class);
    }

    @Override
    public Page<AccountDetailsResponseDTO> getAllAccounts(String PageReq , String PageSizeReq) {
        int page = 1;
        int pageSize = 8;


        try {
            if (PageReq != null) {
                page = Integer.parseInt(PageReq);
                if(page < 1 ) throw new RuntimeException("Error: page and/or pageSize must be greater than 0");
            }
            if (PageSizeReq != null) {
                pageSize = Integer.parseInt(PageSizeReq);
                if(pageSize < 1) throw new RuntimeException("Error: page and/or pageSize must be greater than 0");
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
    public List<AccountDetailsResponseDTO> searchAccount(String key) {
        if(key == null) throw new IllegalArgumentException("key is null");
        List<AccountDetails> AccountList = accountRepo.searchAccounts(key);
        List<AccountDetailsResponseDTO> list = new ArrayList<>();
        ModelMapper modelMapper = new ModelMapper();
        if(AccountList.isEmpty()) return list;
        List<AccountDetailsResponseDTO> ResponseList
                = AccountList.stream()
                .map(accountDetails -> modelMapper.map(accountDetails, AccountDetailsResponseDTO.class))
                .collect(Collectors.toList());

        return ResponseList;
    }
}


