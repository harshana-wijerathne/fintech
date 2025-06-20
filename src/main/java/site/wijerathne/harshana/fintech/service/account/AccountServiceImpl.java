package site.wijerathne.harshana.fintech.service.account;

import org.modelmapper.ModelMapper;
import site.wijerathne.harshana.fintech.dto.AuditLogDTO;
import site.wijerathne.harshana.fintech.dto.account.AccountDetailsRequestDTO;
import site.wijerathne.harshana.fintech.dto.account.AccountDetailsResponseDTO;
import site.wijerathne.harshana.fintech.dto.account.AccountRequestDTO;
import site.wijerathne.harshana.fintech.exception.EntityNotFoundException;
import site.wijerathne.harshana.fintech.exception.account.*;
import site.wijerathne.harshana.fintech.model.Account;
import site.wijerathne.harshana.fintech.model.AccountDetails;
import site.wijerathne.harshana.fintech.model.Customer;
import site.wijerathne.harshana.fintech.model.User;
import site.wijerathne.harshana.fintech.repo.AuditLogRepo;
import site.wijerathne.harshana.fintech.repo.account.AccountRepo;
import site.wijerathne.harshana.fintech.repo.customer.CustomerRepo;
import site.wijerathne.harshana.fintech.util.AccountNumberGenerator;
import site.wijerathne.harshana.fintech.util.Page;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class AccountServiceImpl implements AccountService {
    private static final Logger logger = Logger.getLogger(AccountServiceImpl.class.getName());
    private final AccountRepo accountRepo;
    private final CustomerRepo customerRepo;
    private final AuditLogRepo auditLogRepo;
    private final ModelMapper modelMapper = new ModelMapper();

    public AccountServiceImpl(AccountRepo accountRepo, CustomerRepo customerRepo, AuditLogRepo auditLogRepo) {
        this.accountRepo = accountRepo;
        this.customerRepo = customerRepo;
        this.auditLogRepo = auditLogRepo;
    }

    @Override
    public AccountDetailsResponseDTO getAccountByAccountNumber(String accountNumber) {
        if (accountNumber == null || accountNumber.trim().isEmpty()) {
            logger.log(Level.WARNING, "Account number is null or empty");
            throw new IllegalArgumentException("Account number is required");
        }

        try {
            Optional<AccountDetails> accountDetails = accountRepo.getAccountDetailsById(accountNumber);
            if (accountDetails.isEmpty()) {
                logger.log(Level.WARNING, "Account not found with number: " + accountNumber);
                throw new AccountNotFoundException(accountNumber);
            }
            return modelMapper.map(accountDetails.get(), AccountDetailsResponseDTO.class);
        } catch (AccountNotFoundException e) {
            throw e;
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error fetching account with number: " + accountNumber, e);
            throw new AccountServiceException("Error fetching account details", e);
        }
    }

    @Override
    public Page<AccountDetailsResponseDTO> getAllAccounts(String pageReq, String pageSizeReq) {
        int page = 1;
        int pageSize = 8;

        try {
            if (pageReq != null) {
                page = Integer.parseInt(pageReq);
                if (page < 1) {
                    throw new IllegalArgumentException("Page must be greater than 0");
                }
            }
            if (pageSizeReq != null) {
                pageSize = Integer.parseInt(pageSizeReq);
                if (pageSize < 1) {
                    throw new IllegalArgumentException("Page size must be greater than 0");
                }
            }
        } catch (NumberFormatException e) {
            logger.log(Level.WARNING, "Invalid pagination parameters", e);
            throw new IllegalArgumentException("Invalid pagination parameters");
        }

        try {
            Page<AccountDetails> allAccountsDetails = accountRepo.getAllAccountsDetails(page, pageSize);
            List<AccountDetailsResponseDTO> accountDetailsResponseDTOList = allAccountsDetails.getContent().stream()
                    .map(account -> modelMapper.map(account, AccountDetailsResponseDTO.class))
                    .collect(Collectors.toList());

            return new Page<>(
                    accountDetailsResponseDTOList,
                    allAccountsDetails.getCurrentPage(),
                    allAccountsDetails.getPageSize(),
                    allAccountsDetails.getTotalRecords(),
                    allAccountsDetails.getTotalPages()
            );
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error fetching all accounts", e);
            throw new AccountServiceException("Error fetching accounts", e);
        }
    }

    @Override
    public AccountDetailsResponseDTO saveAccount(AccountRequestDTO dto, HttpServletRequest req) {
        try {
            validateAccountRequest(dto);

            Customer customer = customerRepo.getCustomerById(dto.getCustomerId());
            if (customer == null) {
                logger.log(Level.WARNING, "Customer not found with ID: " + dto.getCustomerId());
                throw new EntityNotFoundException("Customer not found with ID: " + dto.getCustomerId());
            }

            String accountNumber = generateUniqueAccountNumber(dto);
            Account account = modelMapper.map(dto, Account.class);
            account.setAccountNumber(accountNumber);

            Account savedAccount = accountRepo.saveAccount(account);

            AccountDetailsResponseDTO response = modelMapper.map(customer, AccountDetailsResponseDTO.class);
            response.setAccountNumber(savedAccount.getAccountNumber());
            response.setOpeningDate(savedAccount.getOpeningDate());
            response.setCustomerId(savedAccount.getCustomerId());
            response.setBalance(savedAccount.getBalance());

            logAudit("CREATE", accountNumber, req, "Created account for customer: " + dto.getCustomerId());

            return response;
        } catch (EntityNotFoundException e) {
            throw e;
        } catch (IllegalArgumentException e) {
            logger.log(Level.WARNING, "Validation error while creating account: " + e.getMessage());
            throw e;
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error creating account", e);
            throw new AccountCreationException("Error creating account", e);
        }
    }

    @Override
    public AccountDetailsResponseDTO updateAccountDetails(AccountDetailsRequestDTO accountDetails, HttpServletRequest req) {
        try {
            if (accountDetails == null) {
                throw new IllegalArgumentException("Account details cannot be null");
            }

            if (accountDetails.getAccountNumber() == null || accountDetails.getAccountNumber().trim().isEmpty()) {
                throw new IllegalArgumentException("Account number is required");
            }

            Optional<Account> existingAccountOpt = accountRepo.getAccountById(accountDetails.getAccountNumber());
            if (existingAccountOpt.isEmpty()) {
                logger.log(Level.WARNING, "Account not found: " + accountDetails.getAccountNumber());
                throw new AccountNotFoundException(accountDetails.getAccountNumber());
            }

            Customer existingCustomer = customerRepo.getCustomerById(accountDetails.getCustomerId());
            if (existingCustomer == null) {
                logger.log(Level.WARNING, "Customer not found: " + accountDetails.getCustomerId());
                throw new EntityNotFoundException("Customer not found: " + accountDetails.getCustomerId());
            }

            Account newAccount = modelMapper.map(accountDetails, Account.class);
            newAccount.setOpeningDate(existingAccountOpt.get().getOpeningDate());

            Account updatedAccount = accountRepo.updateAccountDetails(newAccount);

            AccountDetailsResponseDTO response = modelMapper.map(updatedAccount, AccountDetailsResponseDTO.class);
            response.setAccountNumber(updatedAccount.getAccountNumber());
            response.setOpeningDate(updatedAccount.getOpeningDate());
            response.setBalance(updatedAccount.getBalance());

            logAudit("UPDATE", updatedAccount.getAccountNumber(), req,
                    "Updated account for customer ID: " + updatedAccount.getCustomerId());

            return response;
        } catch (AccountNotFoundException | EntityNotFoundException e) {
            throw e;
        } catch (IllegalArgumentException e) {
            logger.log(Level.WARNING, "Validation error while updating account: " + e.getMessage());
            throw e;
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error updating account details", e);
            throw new AccountUpdateException("Error updating account details", e);
        }
    }

    @Override
    public boolean deleteAccountById(String accountNumber, HttpServletRequest req) {
        try {
            if (accountNumber == null || accountNumber.trim().isEmpty()) {
                throw new IllegalArgumentException("Account number must not be null or empty");
            }

            logger.log(Level.INFO, "Attempting to delete account with number: " + accountNumber);

            boolean deleted = accountRepo.deleteAccount(accountNumber);
            if (!deleted) {
                logger.log(Level.WARNING, "Delete failed: No account found with number: " + accountNumber);
                throw new AccountNotFoundException(accountNumber);
            }

            logger.log(Level.INFO, "Account deleted successfully: " + accountNumber);
            logAudit("DELETE", accountNumber, req, "Deleted account: " + accountNumber);

            return true;
        } catch (AccountNotFoundException e) {
            throw e;
        } catch (IllegalArgumentException e) {
            logger.log(Level.WARNING, "Validation error while deleting account: " + e.getMessage());
            throw e;
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error deleting account: " + accountNumber, e);
            throw new AccountDeletionException("Error deleting account", e);
        }
    }

    @Override
    public List<AccountDetailsResponseDTO> searchAccount(String key) {
        try {
            if (key == null || key.trim().isEmpty()) {
                throw new IllegalArgumentException("Search key cannot be null or empty");
            }

            List<AccountDetails> accountList = accountRepo.searchAccounts(key);
            if (accountList.isEmpty()) {
                return new ArrayList<>();
            }

            return accountList.stream()
                    .map(account -> modelMapper.map(account, AccountDetailsResponseDTO.class))
                    .collect(Collectors.toList());
        } catch (IllegalArgumentException e) {
            logger.log(Level.WARNING, "Invalid search parameter: " + e.getMessage());
            throw e;
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error searching accounts with key: " + key, e);
            throw new AccountServiceException("Error searching accounts", e);
        }
    }

    private void validateAccountRequest(AccountRequestDTO dto) {
        if (dto == null) throw new IllegalArgumentException("Request body is missing.");
        if (dto.getAccountType() == null) throw new IllegalArgumentException("Account type is required.");
        if (dto.getOpeningDate() == null) throw new IllegalArgumentException("Opening date is required.");
        if (dto.getCustomerId() == null) throw new IllegalArgumentException("Customer ID is required.");
    }

    private String generateUniqueAccountNumber(AccountRequestDTO dto) {
        String accountNumber;
        do {
            accountNumber = AccountNumberGenerator.generateAccountNumber(dto);
        } while (accountRepo.getAccountById(accountNumber).isPresent());
        return accountNumber;
    }

    private void logAudit(String actionType, String entityId, HttpServletRequest req, String description) {
        try {
            User user = (User) req.getSession().getAttribute("username");
            AuditLogDTO auditLog = AuditLogDTO.builder()
                    .actorUserId(user.getUsername())
                    .actionType(actionType)
                    .entityType("ACCOUNT")
                    .entityId(entityId)
                    .description(description)
                    .ipAddress(req.getRemoteAddr())
                    .build();
            auditLogRepo.saveAuditLog(auditLog);
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Failed to update audit log for account: " + entityId, e);
        }
    }
}