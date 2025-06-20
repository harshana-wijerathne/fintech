package site.wijerathne.harshana.fintech.service.transaction;


import org.modelmapper.ModelMapper;
import site.wijerathne.harshana.fintech.dto.AuditLogDTO;
import site.wijerathne.harshana.fintech.dto.transaction.TransactionRequestDTO;
import site.wijerathne.harshana.fintech.dto.transaction.TransactionResponseDTO;
import site.wijerathne.harshana.fintech.exception.DataAccessException;
import site.wijerathne.harshana.fintech.exception.transaction.*;
import site.wijerathne.harshana.fintech.model.Transaction;
import site.wijerathne.harshana.fintech.model.User;
import site.wijerathne.harshana.fintech.repo.AuditLogRepo;
import site.wijerathne.harshana.fintech.repo.transaction.TransactionRepo;
import site.wijerathne.harshana.fintech.util.Page;

import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.sql.Date;
import java.util.List;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class TransactionServiceImpl implements TransactionService {
    private static final Logger logger = Logger.getLogger(TransactionServiceImpl.class.getName());
    private final TransactionRepo transactionRepo;
    private final AuditLogRepo auditLogger;

    public TransactionServiceImpl(TransactionRepo transactionRepo, AuditLogRepo auditLogger) {
        this.transactionRepo = transactionRepo;
        this.auditLogger = auditLogger;
    }

    @Override
    public TransactionResponseDTO deposit(TransactionRequestDTO requestDTO, HttpServletRequest request) {
        if(requestDTO == null) throw new InvalidTransactionException("Request is null");
        if (requestDTO.getAmount() == null || requestDTO.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new InvalidTransactionException("Deposit amount must be positive");
        }
        User username = (User) request.getSession().getAttribute("username");
        try {
            ModelMapper modelMapper = new ModelMapper();
            Transaction transactionReq = modelMapper.map(requestDTO, Transaction.class);


            transactionReq.setReferenceNumber(UUID.randomUUID().toString());


            Transaction transactionResp = transactionRepo.deposit(transactionReq);
            auditLogger.saveAuditLog(new AuditLogDTO(username.getUsername(), "UPDATE", "TRANSACTION","DEPOSIT", "Deposit completed",request.getRemoteAddr()));
            logger.log(Level.INFO, "Deposit of {0} to account {1} completed",
                    new Object[]{transactionResp.getAmount(), transactionResp.getAccountNumber()});

            return modelMapper.map(transactionResp, TransactionResponseDTO.class);
        } catch (Exception e) {
            auditLogger.saveAuditLog(new AuditLogDTO(username.getUsername(), "UPDATE", "TRANSACTION","DEPOSIT", "Deposit completed",request.getRemoteAddr()));
            logger.log(Level.SEVERE, "Deposit failed for account: " + requestDTO.getAccountNumber(), e);
            throw e;
        }
    }

    @Override
    public TransactionResponseDTO withdraw(TransactionRequestDTO requestDTO) {
        if(requestDTO == null) throw new InvalidTransactionException("Request is null");

        if (requestDTO.getAmount() == null || requestDTO.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new InvalidTransactionException("Withdrawal amount must be positive");
        }



        try {

            ModelMapper modelMapper = new ModelMapper();
            Transaction transactionReq = modelMapper.map(requestDTO, Transaction.class);
            transactionReq.setReferenceNumber(UUID.randomUUID().toString());

            Transaction updatedAccount = transactionRepo.withdraw(transactionReq);
            auditLogger.saveAuditLog(new AuditLogDTO(transactionReq.getAccountNumber(), "UPDATE", "ACCOUNT","WITHDRAW", "Withdrawal completed",null));
            logger.log(Level.INFO, "Withdrawal of {0} from account {1} completed",
                    new Object[]{transactionReq.getAmount(), transactionReq.getAccountNumber()});
            return modelMapper.map(updatedAccount, TransactionResponseDTO.class);
        } catch (InsufficientFundsException e) {
            auditLogger.saveAuditLog(new AuditLogDTO(requestDTO.getAccountNumber(), "UPDATE", "ACCOUNT",
                    "WITHDRAWAL_FAILED","Insufficient funds",null));
            logger.log(Level.WARNING, "Insufficient funds for withdrawal from account: " + requestDTO.getAccountNumber());
            throw e;
        } catch (Exception e) {
            auditLogger.saveAuditLog(new AuditLogDTO(requestDTO.getAccountNumber(), "UPDATE", "ACCOUNT","WITHDRAWAL_FAILED",
                    "Withdrawal failed: " + e.getMessage(),null));
            logger.log(Level.SEVERE, "Withdrawal failed for account: " + requestDTO.getAccountNumber(), e);
            throw e;
        }
    }

    @Override
    public Page<TransactionResponseDTO> getAllTransactions(int page, int pageSize)
            throws TransactionServiceException {
        try {
            validatePaginationParameters(page, pageSize);
            Page<Transaction> transactions = transactionRepo.getAllTransactions(page, pageSize);
            ModelMapper modelMapper = new ModelMapper();
            modelMapper.map(transactions, TransactionResponseDTO.class);
            return mapToResponsePage(transactions);
        } catch (DataAccessException e) {
            logger.log(Level.SEVERE, "Error retrieving all transactions", e);
            throw new TransactionServiceException("Failed to retrieve transactions", e);
        }
    }

    @Override
    public Page<TransactionResponseDTO> getTransactionsByAccountNumber(String accountNumber, int page, int pageSize)
            throws AccountNotFoundException, TransactionServiceException {
        try {
            validatePaginationParameters(page, pageSize);
            if (accountNumber == null || accountNumber.trim().isEmpty()) {
                throw new IllegalArgumentException("Account number is required");
            }

            Page<Transaction> transactions = transactionRepo.getTransactionsByAccountNumber(
                    accountNumber, page, pageSize);
            return mapToResponsePage(transactions);
        } catch (DataAccessException e) {
            if (e.getMessage().contains("No transactions found")) {
                throw new AccountNotFoundException("Account not found or has no transactions: " + accountNumber);
            }
            logger.log(Level.SEVERE, "Error retrieving transactions for account: " + accountNumber, e);
            throw new TransactionServiceException("Failed to retrieve transactions", e);
        }
    }

    @Override
    public Page<TransactionResponseDTO> getTransactionsByDateRange(String accountNumber, Date startDate,
                                                                   Date endDate, int page, int pageSize) throws AccountNotFoundException,
            InvalidDateRangeException, TransactionServiceException {

        try {
            validatePaginationParameters(page, pageSize);
            validateDateRange(startDate, endDate);

            Page<Transaction> transactions = transactionRepo.getTransactionsByDateRange(
                    accountNumber, startDate, endDate, page, pageSize);
            return mapToResponsePage(transactions);
        } catch (DataAccessException e) {
            if (e.getMessage().contains("No transactions found")) {
                String message = accountNumber != null ?
                        "Account not found or has no transactions in date range" :
                        "No transactions found in date range";
                throw new AccountNotFoundException(message);
            }
            logger.log(Level.SEVERE, "Error retrieving transactions by date range", e);
            throw new TransactionServiceException("Failed to retrieve transactions", e);
        }
    }


    /*---------------Supportive Functions---------------*/

    private Page<TransactionResponseDTO> mapToResponsePage(Page<Transaction> transactionPage) {
        List<TransactionResponseDTO> content = transactionPage.getContent().stream()
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());

        return new Page<>(
                content,
                transactionPage.getCurrentPage(),
                transactionPage.getPageSize(),
                transactionPage.getTotalRecords(),
                transactionPage.getTotalPages()
        );
    }

    private TransactionResponseDTO mapToResponseDTO(Transaction transaction) {
        TransactionResponseDTO response = new TransactionResponseDTO();
        response.setTransactionId(transaction.getTransactionId());
        response.setAccountNumber(transaction.getAccountNumber());
        response.setAmount(transaction.getAmount());
        response.setTransactionType(transaction.getTransactionType());
        response.setDescription(transaction.getDescription());
        response.setCreatedAt(transaction.getCreatedAt());
        response.setReferenceNumber(transaction.getReferenceNumber());
        response.setBalance(transaction.getBalance());
        System.out.println(response.toString());
        return response;
    }

    private void validatePaginationParameters(int page, int pageSize) {
        if (page < 1) {
            throw new IllegalArgumentException("Page must be greater than 0");
        }
        if (pageSize < 1 || pageSize > 100) {
            throw new IllegalArgumentException("Page size must be between 1 and 100");
        }
    }

    private void validateDateRange(Date startDate, Date endDate) throws InvalidDateRangeException {
        if (startDate == null || endDate == null) {
            throw new InvalidDateRangeException("Both start and end dates are required");
        }
        if (startDate.after(endDate)) {
            throw new InvalidDateRangeException("Start date must be before end date");
        }
    }


}