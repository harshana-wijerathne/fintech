package site.wijerathne.harshana.fintech.repo.transaction;

import com.zaxxer.hikari.HikariDataSource;
import site.wijerathne.harshana.fintech.exception.DataAccessException;
import site.wijerathne.harshana.fintech.exception.transaction.InsufficientFundsException;
import site.wijerathne.harshana.fintech.model.Transaction;
import site.wijerathne.harshana.fintech.util.Page;

import java.math.BigDecimal;
import java.sql.*;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class TransactionRepoImpl implements TransactionRepo {
    private static final Logger logger = Logger.getLogger(TransactionRepoImpl.class.getName());
    private final HikariDataSource connectionPool;

    public TransactionRepoImpl(HikariDataSource connectionPool) {
        this.connectionPool = connectionPool;
    }

    @Override
    public Transaction deposit(Transaction transaction) {

        final String selectSql = "SELECT balance FROM saving_accounts WHERE account_number = ? FOR UPDATE";
        final String updateSql = "UPDATE saving_accounts SET balance = balance + ?, updated_at = ? WHERE account_number = ?";
        final String insertTxSql = "INSERT INTO transactions " +
                "(account_number, amount, new_balance, transaction_type, description,reference_number) " +
                "VALUES (?, ?,?, 'DEPOSIT', ?, ?)";

        try (Connection connection = connectionPool.getConnection()) {
            connection.setAutoCommit(false);

            try {
                // 1. Lock the account row for update
                try (PreparedStatement selectStmt = connection.prepareStatement(selectSql)) {
                    selectStmt.setString(1, transaction.getAccountNumber());
                    var rs = selectStmt.executeQuery();
                    if (!rs.next()) {
                        throw new DataAccessException("Account not found: " + transaction.getAccountNumber(),new Throwable());
                    }

                    transaction.setAccountNumber(transaction.getAccountNumber());
                    transaction.setBalance(rs.getBigDecimal("balance"));
                }

                // 2. Update account balance
                try (PreparedStatement updateStmt = connection.prepareStatement(updateSql)) {
                    updateStmt.setBigDecimal(1, transaction.getAmount());
                    updateStmt.setTimestamp(2, Timestamp.from(Instant.now()));
                    updateStmt.setString(3, transaction.getAccountNumber());

                    int rowsUpdated = updateStmt.executeUpdate();
                    if (rowsUpdated != 1) {
                        throw new DataAccessException("Failed to update account balance",new Throwable());
                    }
                }

                // 3. Record transaction
                try (PreparedStatement txStmt = connection.prepareStatement(insertTxSql)) {
                    txStmt.setString(1, transaction.getAccountNumber());
                    txStmt.setBigDecimal(2, transaction.getAmount());
                    txStmt.setBigDecimal(3, transaction.getBalance().add(transaction.getAmount()));
                    txStmt.setString(4, transaction.getDescription());
                    txStmt.setString(5, transaction.getReferenceNumber());

                    txStmt.executeUpdate();
                }

                // 4. Get updated balance
                try (PreparedStatement selectStmt = connection.prepareStatement(
                        "SELECT balance FROM saving_accounts WHERE account_number = ?")) {
                    selectStmt.setString(1, transaction.getAccountNumber());
                    var rs = selectStmt.executeQuery();
                    if (rs.next()) {
                        transaction.setBalance(rs.getBigDecimal("balance"));
                    }
                }

                connection.commit();
                return transaction;
            } catch (SQLException e) {
                connection.rollback();
                logger.log(Level.SEVERE, "Transaction failed for account: " + transaction.getAccountNumber(), e);
                throw new DataAccessException("Transaction processing failed", e);
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Database connection error", e);
            throw new DataAccessException("Database connection error", e);
        }
    }

    @Override
    public Transaction withdraw(Transaction transaction) {
        final String selectSql = "SELECT balance FROM saving_accounts WHERE account_number = ? FOR UPDATE";

        final String updateSql = "UPDATE saving_accounts SET balance = balance - ?, updated_at = ? " +
                "WHERE account_number = ? AND balance >= ?";

        final String insertTxSql = "INSERT INTO transactions " +
                "(account_number, amount, new_balance, transaction_type, description,reference_number) " +
                "VALUES (?, ?,?, 'WITHDRAWAL', ?, ?)";

        try (Connection connection = connectionPool.getConnection()) {
            connection.setAutoCommit(false);

            try {
                try (PreparedStatement selectStmt = connection.prepareStatement(selectSql)) {
                    selectStmt.setString(1, transaction.getAccountNumber());
                    var rs = selectStmt.executeQuery();
                    if (!rs.next()) {
                        throw new DataAccessException("Account not found: " + transaction.getAccountNumber(),new Throwable());
                    }
                    BigDecimal currentBalance = rs.getBigDecimal("balance");
                    if (currentBalance.compareTo(transaction.getAmount()) < 0) {
                        throw new InsufficientFundsException("Insufficient funds in account: " + transaction.getAccountNumber());
                    }

                    transaction.setAccountNumber(transaction.getAccountNumber());
                    transaction.setBalance(currentBalance);
                }

                // 2. Update account balance with balance check
                try (PreparedStatement updateStmt = connection.prepareStatement(updateSql)) {
                    updateStmt.setBigDecimal(1, transaction.getAmount());
                    updateStmt.setTimestamp(2, Timestamp.from(Instant.now()));
                    updateStmt.setString(3, transaction.getAccountNumber());
                    updateStmt.setBigDecimal(4, transaction.getAmount());

                    int rowsUpdated = updateStmt.executeUpdate();
                    if (rowsUpdated != 1) {
                        throw new DataAccessException("Failed to update account balance",new Throwable());
                    }
                }

                // 3. Record transaction
                try (PreparedStatement txStmt = connection.prepareStatement(insertTxSql)) {
                    txStmt.setString(1, transaction.getAccountNumber());
                    txStmt.setBigDecimal(2, transaction.getAmount());
                    txStmt.setBigDecimal(3, transaction.getBalance().add(transaction.getAmount()));
                    txStmt.setString(4, transaction.getDescription());
                    txStmt.setString(5, transaction.getReferenceNumber());

                    txStmt.executeUpdate();
                }

                // 4. Get updated balance
                try (PreparedStatement selectStmt = connection.prepareStatement(
                        "SELECT balance FROM saving_accounts WHERE account_number = ?")) {
                    selectStmt.setString(1, transaction.getAccountNumber());
                    var rs = selectStmt.executeQuery();
                    if (rs.next()) {
                        transaction.setBalance(rs.getBigDecimal("balance"));
                    }
                }

                connection.commit();
                return transaction;
            } catch (SQLException e) {
                connection.rollback();
                logger.log(Level.SEVERE, "Transaction failed for account: " + transaction.getAccountNumber(), e);
                throw new DataAccessException("Transaction processing failed", e);
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Database connection error", e);
            throw new DataAccessException("Database connection error", e);
        }
    }

    @Override
    public Page<Transaction> getAllTransactions(int page, int pageSize) throws DataAccessException {
        final String sql = "SELECT * FROM transactions ORDER BY created_at DESC LIMIT ? OFFSET ?";
        final String countSql = "SELECT COUNT(*) FROM transactions";

        try (Connection connection = connectionPool.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql);
             PreparedStatement countStmt = connection.prepareStatement(countSql)) {

            // Calculate pagination values
            int offset = (page - 1) * pageSize;
            stmt.setInt(1, pageSize);
            stmt.setInt(2, offset);

            // Execute query
            List<Transaction> transactions = new ArrayList<>();
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    transactions.add(mapTransaction(rs));
                }
            }

            // Get total count
            int totalRecords = 0;
            try (ResultSet rs = countStmt.executeQuery()) {
                if (rs.next()) {
                    totalRecords = rs.getInt(1);
                }
            }

            return new Page<>(
                    transactions,
                    page,
                    pageSize,
                    totalRecords,
                    (int) Math.ceil((double) totalRecords / pageSize)
            );

        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error retrieving all transactions", e);
            throw new DataAccessException("Failed to retrieve transactions", e);
        }
    }

    @Override
    public Page<Transaction> getTransactionsByAccountNumber(String accountNumber, int page, int pageSize)
            throws DataAccessException {
        final String sql = "SELECT * FROM transactions WHERE account_number = ? ORDER BY created_at DESC LIMIT ? OFFSET ?";
        final String countSql = "SELECT COUNT(*) FROM transactions WHERE account_number = ?";

        try (Connection connection = connectionPool.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql);
             PreparedStatement countStmt = connection.prepareStatement(countSql)) {

            // Set parameters
            int offset = (page - 1) * pageSize;
            stmt.setString(1, accountNumber);
            stmt.setInt(2, pageSize);
            stmt.setInt(3, offset);
            countStmt.setString(1, accountNumber);

            // Execute query
            List<Transaction> transactions = new ArrayList<>();
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    transactions.add(mapTransaction(rs));
                }
            }

            // Get total count
            int totalRecords = 0;
            try (ResultSet rs = countStmt.executeQuery()) {
                if (rs.next()) {
                    totalRecords = rs.getInt(1);
                }
            }

            if (transactions.isEmpty() && page == 1) {
                throw new DataAccessException("No transactions found for account: " + accountNumber, new Throwable());
            }

            return new Page<>(
                    transactions,
                    page,
                    pageSize,
                    totalRecords,
                    (int) Math.ceil((double) totalRecords / pageSize)
            );

        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error retrieving transactions for account: " + accountNumber, e);
            throw new DataAccessException("Failed to retrieve transactions", e);
        }
    }

    @Override
    public Page<Transaction> getTransactionsByDateRange(String accountNumber, Date startDate, Date endDate,
                                                        int page, int pageSize) throws DataAccessException {

        StringBuilder sqlBuilder = new StringBuilder(
                "SELECT * FROM transactions WHERE created_at BETWEEN ? AND ? ");
        StringBuilder countSqlBuilder = new StringBuilder(
                "SELECT COUNT(*) FROM transactions WHERE created_at BETWEEN ? AND ? ");

        if (accountNumber != null) {
            sqlBuilder.append("AND account_number = ? ");
            countSqlBuilder.append("AND account_number = ? ");
        }
        sqlBuilder.append("ORDER BY created_at DESC LIMIT ? OFFSET ?");

        try (Connection connection = connectionPool.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sqlBuilder.toString());
             PreparedStatement countStmt = connection.prepareStatement(countSqlBuilder.toString())) {

            // Set common parameters
            int paramIndex = 1;
            stmt.setDate(paramIndex, startDate);
            countStmt.setDate(paramIndex++, startDate);
            stmt.setDate(paramIndex, endDate);
            countStmt.setDate(paramIndex++, endDate);

            // Set account number if provided
            if (accountNumber != null) {
                stmt.setString(paramIndex, accountNumber);
                countStmt.setString(paramIndex++, accountNumber);
            }

            // Set pagination parameters
            int offset = (page - 1) * pageSize;
            stmt.setInt(paramIndex++, pageSize);
            stmt.setInt(paramIndex, offset);

            // Execute query
            List<Transaction> transactions = new ArrayList<>();
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    transactions.add(mapTransaction(rs));
                }
            }

            // Get total count
            int totalRecords = 0;
            try (ResultSet rs = countStmt.executeQuery()) {
                if (rs.next()) {
                    totalRecords = rs.getInt(1);
                }
            }

            if (transactions.isEmpty() && page == 1) {
                throw new DataAccessException("No transactions found for the given criteria",new Throwable());
            }

            return new Page<>(
                    transactions,
                    page,
                    pageSize,
                    totalRecords,
                    (int) Math.ceil((double) totalRecords / pageSize)
            );

        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error retrieving transactions by date range", e);
            throw new DataAccessException("Failed to retrieve transactions", e);
        }
    }

    private Transaction mapTransaction(ResultSet rs) throws SQLException {
        Transaction transaction = new Transaction();
        transaction.setTransactionId(rs.getString("id"));
        transaction.setAccountNumber(rs.getString("account_number"));
        transaction.setAmount(rs.getBigDecimal("amount"));
        transaction.setTransactionType(rs.getString("transaction_type"));
        transaction.setDescription(rs.getString("description"));
        transaction.setCreatedAt(rs.getTimestamp("created_at"));
        return transaction;
    }

}



