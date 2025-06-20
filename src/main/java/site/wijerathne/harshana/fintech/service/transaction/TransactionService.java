package site.wijerathne.harshana.fintech.service.transaction;

import site.wijerathne.harshana.fintech.dto.transaction.TransactionRequestDTO;
import site.wijerathne.harshana.fintech.dto.transaction.TransactionResponseDTO;
import site.wijerathne.harshana.fintech.exception.transaction.AccountNotFoundException;
import site.wijerathne.harshana.fintech.exception.transaction.InvalidDateRangeException;
import site.wijerathne.harshana.fintech.exception.transaction.TransactionServiceException;
import site.wijerathne.harshana.fintech.model.Account;
import site.wijerathne.harshana.fintech.util.Page;

import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.sql.Date;

public interface TransactionService {

    TransactionResponseDTO deposit(TransactionRequestDTO requestDTO, HttpServletRequest request);

    TransactionResponseDTO withdraw(TransactionRequestDTO requestDTO);

    Page<TransactionResponseDTO> getAllTransactions(int page, int pageSize)
            throws TransactionServiceException;

    Page<TransactionResponseDTO> getTransactionsByAccountNumber(String accountNumber, int page, int pageSize)
            throws AccountNotFoundException, TransactionServiceException;

    Page<TransactionResponseDTO> getTransactionsByDateRange(String accountNumber, Date startDate,
                                                            Date endDate, int page, int pageSize) throws AccountNotFoundException,
            InvalidDateRangeException, TransactionServiceException;
}
