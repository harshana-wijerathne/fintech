package site.wijerathne.harshana.fintech.repo;

import site.wijerathne.harshana.fintech.model.SavingAccount;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class SavingAccountRepo {
    private static final Logger logger = Logger.getLogger(SavingAccountRepo.class.getName());

    public List<SavingAccount> getAllSavingAccounts(Connection connection) {
        String sql = "SELECT * FROM saving_accounts ORDER BY created_at DESC";
        List<SavingAccount> accounts = new ArrayList<>();

        try (PreparedStatement stmt = connection.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                accounts.add(extractAccount(rs));
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error fetching all saving accounts", e);
        }
        return accounts;
    }

    public SavingAccount getSavingAccountById(String accountNumber, Connection connection) {
        String sql = "SELECT * FROM saving_accounts WHERE account_number = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, accountNumber);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return extractAccount(rs);
                }
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error fetching saving account by ID: " + accountNumber, e);
        }
        return null;
    }

    public boolean saveSavingAccount(SavingAccount account, Connection connection) {
        String sql = "INSERT INTO saving_accounts (account_number, customer_id, opening_date, account_type, balance, created_at, updated_at) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?)";
        boolean autoCommit = false;

        try {
            autoCommit = connection.getAutoCommit();
            connection.setAutoCommit(false);

            try (PreparedStatement stmt = connection.prepareStatement(sql)) {
                stmt.setString(1, account.getAccountNumber());
                stmt.setString(2, account.getCustomerId());
                stmt.setTimestamp(3, account.getOpeningDate());
                stmt.setString(4, account.getAccountType());
                stmt.setBigDecimal(5, account.getBalance());
                int rows = stmt.executeUpdate();
                connection.commit();
                return rows > 0;
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error saving saving account", e);
            try {
                connection.rollback();
            } catch (SQLException rollbackEx) {
                logger.log(Level.SEVERE, "Error during rollback", rollbackEx);
            }
        } finally {
            try {
                connection.setAutoCommit(autoCommit);
            } catch (SQLException e) {
                logger.log(Level.SEVERE, "Failed to restore auto-commit", e);
            }
        }
        return false;
    }

    public boolean updateSavingAccount(SavingAccount account, Connection connection) {
        String sql = "UPDATE saving_accounts SET customer_id = ?, opening_date = ?, account_type = ?, balance = ?, updated_at = ? " +
                "WHERE account_number = ?";
        boolean autoCommit = false;

        try {
            autoCommit = connection.getAutoCommit();
            connection.setAutoCommit(false);

            try (PreparedStatement stmt = connection.prepareStatement(sql)) {
                stmt.setString(1, account.getCustomerId());
                stmt.setTimestamp(2, account.getOpeningDate());
                stmt.setString(3, account.getAccountType());
                stmt.setBigDecimal(4, account.getBalance());
                stmt.setTimestamp(5, account.getUpdatedAt());
                stmt.setString(6, account.getAccountNumber());

                int rows = stmt.executeUpdate();
                connection.commit();
                return rows > 0;
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error updating saving account", e);
            try {
                connection.rollback();
            } catch (SQLException rollbackEx) {
                logger.log(Level.SEVERE, "Error during rollback", rollbackEx);
            }
        } finally {
            try {
                connection.setAutoCommit(autoCommit);
            } catch (SQLException e) {
                logger.log(Level.SEVERE, "Failed to restore auto-commit", e);
            }
        }
        return false;
    }

    public boolean deleteSavingAccount(String accountNumber, Connection connection) {
        String sql = "DELETE FROM saving_accounts WHERE account_number = ?";
        boolean autoCommit = false;

        try {
            autoCommit = connection.getAutoCommit();
            connection.setAutoCommit(false);

            try (PreparedStatement stmt = connection.prepareStatement(sql)) {
                stmt.setString(1, accountNumber);
                int rows = stmt.executeUpdate();
                connection.commit();
                return rows > 0;
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error deleting saving account", e);
            try {
                connection.rollback();
            } catch (SQLException rollbackEx) {
                logger.log(Level.SEVERE, "Error during rollback", rollbackEx);
            }
        } finally {
            try {
                connection.setAutoCommit(autoCommit);
            } catch (SQLException e) {
                logger.log(Level.SEVERE, "Failed to restore auto-commit", e);
            }
        }
        return false;
    }

    public List<SavingAccount> searchSavingAccountsByAccountNumber(String partialAccountNumber, Connection connection) {
        String sql = "SELECT * FROM saving_accounts WHERE account_number LIKE ? ORDER BY created_at DESC";
        List<SavingAccount> accounts = new ArrayList<>();

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, "%" + partialAccountNumber + "%");
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    accounts.add(extractAccount(rs));
                }
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error searching saving accounts by partial account number", e);
        }

        return accounts;
    }

    private SavingAccount extractAccount(ResultSet rs) throws SQLException {
        return SavingAccount.builder()
                .accountNumber(rs.getString("account_number"))
                .customerId(rs.getString("customer_id"))
                .openingDate(rs.getTimestamp("opening_date"))
                .accountType(rs.getString("account_type"))
                .balance(rs.getBigDecimal("balance"))
                .createdAt(rs.getTimestamp("created_at"))
                .updatedAt(rs.getTimestamp("updated_at"))
                .build();
    }
}
