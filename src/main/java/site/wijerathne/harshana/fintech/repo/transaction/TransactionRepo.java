package site.wijerathne.harshana.fintech.repo.transaction;

import site.wijerathne.harshana.fintech.model.Transaction;

import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class TransactionRepo {
    public boolean saveTransaction(String accountNumber, String type, BigDecimal amount, BigDecimal balanceAfter, Connection conn) throws SQLException {
        String sql = "INSERT INTO transactions (transaction_id, account_number, transaction_type, amount, balance_after) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, UUID.randomUUID().toString());
            stmt.setString(2, accountNumber);
            stmt.setString(3, type);
            stmt.setBigDecimal(4, amount);
            stmt.setBigDecimal(5, balanceAfter);
            stmt.executeUpdate();
            return true;
        }catch (SQLException e){
            return false;
        }

    }

    public List<Transaction> getAccountNumberTransactionHistory(String accountNumber, Timestamp from, Timestamp to, Connection conn) throws SQLException {
        String sql = "SELECT * FROM transactions WHERE account_number = ? AND transaction_date BETWEEN ? AND ? ORDER BY transaction_date DESC";
        List<Transaction> transactions = new ArrayList<>();

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, accountNumber);
            stmt.setTimestamp(2, from);
            stmt.setTimestamp(3, to);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    transactions.add(extractTransaction(rs));
                }
            }
        }
        return transactions;
    }

    public List<Transaction> getAllTransactionHistory(Timestamp from, Timestamp to, Connection conn) throws SQLException {
        String sql = "SELECT * FROM transactions WHERE transaction_date BETWEEN ? AND ? ORDER BY transaction_date DESC";
        List<Transaction> transactions = new ArrayList<>();

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setTimestamp(1, from);
            stmt.setTimestamp(2, to);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    transactions.add(extractTransaction(rs));
                }
            }
        }
        return transactions;
    }


    private Transaction extractTransaction(ResultSet rs) throws SQLException {
        return Transaction.builder()
                .transactionId(rs.getString("transaction_id"))
                .accountNumber(rs.getString("account_number"))
                .transactionDate(rs.getTimestamp("transaction_date"))
                .transactionType(rs.getString("transaction_type"))
                .amount(rs.getBigDecimal("amount"))
                .balanceAfter(rs.getBigDecimal("balance_after"))
                .build();
    }


    public BigDecimal getCurrentBalance(String accountNumber, Connection conn) throws SQLException {
        String sql = "SELECT balance FROM saving_accounts WHERE account_number = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, accountNumber);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getBigDecimal("balance");
                }
                throw new SQLException("Account not found: " + accountNumber);
            }
        }
    }

    public void updateAccountBalance(String accountNumber, BigDecimal newBalance, Connection conn) throws SQLException {
        String sql = "UPDATE saving_accounts SET balance = ?, updated_at = CURRENT_TIMESTAMP WHERE account_number = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setBigDecimal(1, newBalance);
            stmt.setString(2, accountNumber);
            stmt.executeUpdate();
        }
    }

}



