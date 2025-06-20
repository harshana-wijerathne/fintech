package site.wijerathne.harshana.fintech.repo.transaction;

import site.wijerathne.harshana.fintech.exception.DataAccessException;
import site.wijerathne.harshana.fintech.model.Account;
import site.wijerathne.harshana.fintech.model.Transaction;
import site.wijerathne.harshana.fintech.util.Page;

import java.math.BigDecimal;
import java.sql.Date;

public interface TransactionRepo {

    Transaction deposit(Transaction transaction);

    Transaction withdraw(Transaction transaction);

    Page<Transaction> getAllTransactions(int page, int pageSize) throws DataAccessException;

    Page<Transaction> getTransactionsByAccountNumber(String accountNumber, int page, int pageSize)
            throws DataAccessException;

    Page<Transaction> getTransactionsByDateRange(String accountNumber, Date startDate, Date endDate,
                                                 int page, int pageSize) throws DataAccessException;
}
