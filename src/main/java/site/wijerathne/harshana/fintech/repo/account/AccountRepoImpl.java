package site.wijerathne.harshana.fintech.repo.account;

import com.zaxxer.hikari.HikariDataSource;
import site.wijerathne.harshana.fintech.dto.account.AccountDetailsResponseDTO;
import site.wijerathne.harshana.fintech.exception.DataAccessException;
import site.wijerathne.harshana.fintech.model.Account;
import site.wijerathne.harshana.fintech.model.AccountDetails;
import site.wijerathne.harshana.fintech.util.Page;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

public class AccountRepoImpl implements AccountRepo {
    private static final Logger logger = Logger.getLogger(AccountRepoImpl.class.getName());
    private final HikariDataSource connectionPool;

    public AccountRepoImpl(HikariDataSource connectionPool) {
        this.connectionPool = connectionPool;
    }

    public Optional<Account> getAccountById(String accountNumber) {
        final String sql = "SELECT * FROM saving_accounts WHERE account_number = ?";

        try (
                Connection connection = connectionPool.getConnection();
                PreparedStatement stmt = connection.prepareStatement(sql)
        ) {
            stmt.setString(1, accountNumber);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapToAccount(rs));
                }
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error fetching account with ID " + accountNumber, e);
            throw new DataAccessException("Failed to retrieve saving account", e);
        }
        return Optional.empty();
    }

    public List<Account> getAllAccounts() {
        final String sql = "SELECT * FROM saving_accounts ORDER BY created_at DESC";
        List<Account> accounts = new ArrayList<>();

        try (
                Connection connection = connectionPool.getConnection();
                PreparedStatement stmt = connection.prepareStatement(sql);
                ResultSet rs = stmt.executeQuery()
        ) {
            while (rs.next()) {
                accounts.add(mapToAccount(rs));
            }
            return accounts;
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Failed to retrieve all saving accounts", e);
            throw new DataAccessException("Error fetching all saving accounts", e);
        }
    }

    public Account saveAccount(Account account) {
        final String sql = "INSERT INTO saving_accounts " +
                "(account_number, customer_id, opening_date, account_type, balance, created_at, updated_at) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (Connection connection = connectionPool.getConnection()) {

            connection.setAutoCommit(false);

            try (PreparedStatement stmt = connection.prepareStatement(sql)) {
                stmt.setString(1, account.getAccountNumber());
                stmt.setString(2, account.getCustomerId());
                stmt.setTimestamp(3, account.getOpeningDate());
                stmt.setString(4, account.getAccountType());
                stmt.setBigDecimal(5, account.getBalance());
                stmt.setTimestamp(6, account.getCreatedAt());
                stmt.setTimestamp(7, account.getUpdatedAt());

                int rows = stmt.executeUpdate();
                if (rows == 0) throw new SQLException("Failed to save saving account");
                connection.commit();
                return account;
            } catch (SQLException e) {
                connection.rollback();
                logger.log(Level.SEVERE, "Error inserting saving account", e);
                throw new DataAccessException("Failed to save saving account", e);
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Failed to obtain connection from pool", e);
            throw new DataAccessException("Connection error while saving account", e);
        }
    }

    public Account updateAccountDetails(Account account) {
        final String sql = "UPDATE saving_accounts SET " +
                "customer_id = ?, opening_date = ?, account_type = ? " +
                "WHERE account_number = ?";

        try (Connection connection = connectionPool.getConnection()) {
            boolean originalAutoCommit = connection.getAutoCommit();
            connection.setAutoCommit(false);

            try (PreparedStatement stmt = connection.prepareStatement(sql)) {
                stmt.setString(1, account.getCustomerId());
                stmt.setTimestamp(2, account.getOpeningDate());
                stmt.setString(3, account.getAccountType());
                stmt.setString(4, account.getAccountNumber());

                int rows = stmt.executeUpdate();
                if (rows == 0) {
                    throw new SQLException("No rows affected while updating saving account");
                }

                connection.commit();
                return account;
            } catch (SQLException e) {
                connection.rollback();
                logger.log(Level.SEVERE, "Error updating saving account: " + account.getAccountNumber(), e);
                throw new DataAccessException("Failed to update saving account", e);
            } finally {
                try {
                    connection.setAutoCommit(originalAutoCommit);
                } catch (SQLException e) {
                    logger.log(Level.SEVERE, "Failed to restore auto-commit after update", e);
                }
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error obtaining DB connection for update", e);
            throw new DataAccessException("Connection failure while updating account", e);
        }
    }

    public boolean deleteAccount(String accountNumber) {
        final String sql = "DELETE FROM saving_accounts WHERE account_number = ?";

        try (Connection connection = connectionPool.getConnection()) {
            boolean originalAutoCommit = connection.getAutoCommit();
            try {
                connection.setAutoCommit(false);

                try (PreparedStatement stmt = connection.prepareStatement(sql)) {
                    stmt.setString(1, accountNumber);
                    int rows = stmt.executeUpdate();
                    connection.commit();
                    return rows > 0;
                } catch (SQLException e) {
                    connection.rollback();
                    logger.log(Level.SEVERE, "Error deleting saving account", e);
                    throw new DataAccessException("Failed to delete saving account", e);
                } finally {
                    connection.setAutoCommit(originalAutoCommit);
                }
            } catch (SQLException e) {
                logger.log(Level.SEVERE, "Transaction failure during delete", e);
                throw new DataAccessException("Transaction failure while deleting account", e);
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Connection failure during delete", e);
            throw new DataAccessException("Failed to obtain connection for deleting account", e);
        }
    }

    public List<AccountDetails> searchAccounts(String search) {
        final String sql = """
        SELECT 
            sa.account_number, sa.opening_date, sa.account_type, sa.balance,
            sa.created_at AS sa_created_at, sa.updated_at AS sa_updated_at,
            c.customer_id, c.full_name, c.nic_passport, c.dob, c.address,
            c.mobile_no, c.email, c.created_at AS c_created_at, c.updated_at AS c_updated_at
        FROM 
            saving_accounts sa
        JOIN 
            customers c ON sa.customer_id = c.customer_id
        WHERE 
            sa.account_number LIKE ? OR c.nic_passport LIKE ?
        LIMIT 20
    """;

        List<AccountDetails> results = new ArrayList<>();


        try (
                Connection conn =  connectionPool.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {
            final String wildcardSearch = "%" + search + "%";
            stmt.setString(1, wildcardSearch);
            stmt.setString(2, wildcardSearch);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    results.add(mapToAccountDetails(rs));
                }
                return results;
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Database error during account search for query: " + search, e);
            throw new DataAccessException("Failed to search accounts", e);
        }


    }

    //---------------------------------------------------------------------------------

    public Page<AccountDetails> getAllAccountsDetails(int page, int size) {
        final String sql = "SELECT sa.account_number, sa.opening_date, sa.account_type, sa.balance, " +
                "sa.created_at AS sa_created_at, sa.updated_at AS sa_updated_at, " +
                "c.customer_id, c.full_name, c.nic_passport, c.dob, c.address, " +
                "c.mobile_no, c.email, c.created_at AS c_created_at, c.updated_at AS c_updated_at " +
                "FROM saving_accounts sa " +
                "JOIN customers c ON sa.customer_id = c.customer_id " +
                "ORDER BY sa.created_at DESC " +
                "LIMIT ? OFFSET ?";

        final String countSql = "SELECT COUNT(*) FROM saving_accounts";

        List<AccountDetails> accountsList = new ArrayList<>();
        int totalRecords = 0;
        int offset = (page - 1) * size;

        try (
                Connection connection = connectionPool.getConnection();
                PreparedStatement stmt = connection.prepareStatement(sql);
                PreparedStatement countStmt = connection.prepareStatement(countSql)
        ) {

            stmt.setInt(1, size);
            stmt.setInt(2, offset);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    accountsList.add(mapToAccountDetails(rs));
                }
            }

            // Get total count
            try (ResultSet countRs = countStmt.executeQuery()) {
                if (countRs.next()) {
                    totalRecords = countRs.getInt(1);
                }
            }

            int totalPages = (int) Math.ceil((double) totalRecords / size);

            return new Page<>(
                    accountsList,
                    page,
                    size,
                    totalRecords,
                    totalPages
            );

        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Failed to retrieve paginated saving accountsList", e);
            throw new DataAccessException("Error fetching paginated saving accountsList", e);
        }
    }

    public Optional<AccountDetails> getAccountDetailsById(String accountNumber) {
        final String sql = "SELECT sa.account_number, sa.opening_date, sa.account_type, sa.balance, " +
                "sa.created_at AS sa_created_at, sa.updated_at AS sa_updated_at, " +
                "c.customer_id, c.full_name, c.nic_passport, c.dob, c.address, " +
                "c.mobile_no, c.email, c.created_at AS c_created_at, c.updated_at AS c_updated_at " +
                "FROM saving_accounts sa " +
                "JOIN customers c ON sa.customer_id = c.customer_id " +
                "WHERE sa.account_number = ?";

        try (
                Connection connection = connectionPool.getConnection();
                PreparedStatement stmt = connection.prepareStatement(sql)
        ) {
            stmt.setString(1, accountNumber);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapToAccountDetails(rs));
                }
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error fetching account with ID " + accountNumber, e);
            throw new DataAccessException("Failed to retrieve saving account", e);
        }
        return Optional.empty();
    }


    /*Support Methods*/

    private Account mapToAccount(ResultSet rs) throws SQLException {
        return Account.builder()
                .accountNumber(rs.getString("account_number"))
                .customerId(rs.getString("customer_id"))
                .openingDate(rs.getTimestamp("opening_date"))
                .accountType(rs.getString("account_type"))
                .balance(rs.getBigDecimal("balance"))
                .createdAt(rs.getTimestamp("created_at"))
                .updatedAt(rs.getTimestamp("updated_at"))
                .build();
    }

    private AccountDetails mapToAccountDetails(ResultSet rs) throws SQLException {
        return AccountDetails.builder()
                .accountNumber(rs.getString("account_number"))
                .openingDate(rs.getTimestamp("opening_date"))
                .accountType(rs.getString("account_type"))
                .balance(rs.getBigDecimal("balance"))
                .accountCreatedAt(rs.getTimestamp("sa_created_at"))
                .accountUpdatedAt(rs.getTimestamp("sa_updated_at"))

                .customerId(rs.getString("customer_id"))
                .fullName(rs.getString("full_name"))
                .nicPassport(rs.getString("nic_passport"))
                .dob(rs.getDate("dob"))
                .address(rs.getString("address"))
                .mobileNo(rs.getString("mobile_no"))
                .email(rs.getString("email"))
                .customerCreatedAt(rs.getTimestamp("c_created_at"))
                .customerUpdatedAt(rs.getTimestamp("c_updated_at"))
                .build();
    }

    private AccountDetailsResponseDTO mapToAccountDetailsDTO(ResultSet rs) throws SQLException {
        AccountDetailsResponseDTO dto = new AccountDetailsResponseDTO();
        dto.setAccountNumber(rs.getString("account_number"));
        dto.setOpeningDate(rs.getTimestamp("opening_date"));
        dto.setAccountType(rs.getString("account_type"));
        dto.setBalance(rs.getBigDecimal("balance"));

        dto.setCustomerId(rs.getString("customer_id"));
        dto.setFullName(rs.getString("full_name"));
        dto.setNicPassport(rs.getString("nic_passport"));
        dto.setDob(rs.getDate("dob"));
        dto.setAddress(rs.getString("address"));
        dto.setMobileNo(rs.getString("mobile_no"));
        dto.setEmail(rs.getString("email"));

        return dto;
    }
}
