//package site.wijerathne.harshana.fintech.service.transaction;
//
//import site.wijerathne.harshana.fintech.model.Account;
//import site.wijerathne.harshana.fintech.repo.account.AccountRepoImpl;
//import site.wijerathne.harshana.fintech.repo.transaction.TransactionRepo;
//import site.wijerathne.harshana.fintech.service.account.AccountServiceImpl;
//
//import java.math.BigDecimal;
//import java.sql.Connection;
//import java.sql.SQLException;
//import java.util.logging.Level;
//import java.util.logging.Logger;
//
//
//public class TransactionServiceImpl {
//    private static final Logger logger = Logger.getLogger(AccountServiceImpl.class.getName());
//    private final TransactionRepo transactionDAO;
//    private final AccountRepoImpl accountDAO;
//
//    public TransactionServiceImpl(TransactionRepo transactionDAO, AccountRepoImpl accountDAO) {
//        this.transactionDAO = transactionDAO;
//        this.accountDAO = accountDAO;
//    }
//
//    public boolean deposit(String accountNumber, BigDecimal amount, Connection conn) throws SQLException {
//        conn.setAutoCommit(false);
//        try {
//            Account savingAccountById = accountDAO.getAccountById(accountNumber, conn);
//            BigDecimal currentBalance = savingAccountById.getBalance();
//            BigDecimal newBalance = currentBalance.add(amount);
//
//            transactionDAO.saveTransaction(accountNumber, "DEPOSIT", amount, newBalance, conn);
////            accountDAO.updateAccountBalance(accountNumber, newBalance, conn);
//
//            conn.commit();
//            return true;
//        } catch (Exception e) {
//            conn.rollback();
//            throw e;
//        }
//    }
//
//    public boolean withdraw(String accountNumber, BigDecimal amount, Connection conn) throws SQLException {
//        boolean originalAutoCommit = conn.getAutoCommit();
//        conn.setAutoCommit(false);
//
//        try {
//            Account savingAccountById = accountDAO.getAccountById(accountNumber, conn);
//            BigDecimal currentBalance = savingAccountById.getBalance();
//
//            if (currentBalance == null) {
//                logger.severe("Account not found: " + accountNumber);
//                throw new IllegalArgumentException("Account not found.");
//            }
//
//            if (amount.compareTo(BigDecimal.ZERO) <= 0) {
//                logger.warning("Invalid withdrawal amount: " + amount);
//                throw new IllegalArgumentException("Amount must be greater than zero.");
//            }
//
//            if (currentBalance.compareTo(amount) < 0) {
//                logger.warning("Insufficient funds for account: " + accountNumber);
//                throw new IllegalArgumentException("Insufficient balance.");
//            }
//
//            BigDecimal newBalance = currentBalance.subtract(amount);
//
//            boolean transactionSaved = transactionDAO.saveTransaction(accountNumber, "WITHDRAW", amount, newBalance, conn);
//            if (!transactionSaved) {
//                logger.severe("Failed to save transaction for account: " + accountNumber);
//                conn.rollback();
//                return false;
//            }
//
////            boolean balanceUpdated = savingAccountDAO.updateAccountBalance(accountNumber, newBalance, conn);
//            if (!true) {
//                logger.severe("Failed to update balance for account: " + accountNumber);
//                conn.rollback();
//                return false;
//            }
//
//            conn.commit();
//            return true;
//
//        } catch (Exception e) {
//            logger.log(Level.SEVERE, "Error during withdrawal for account: " + accountNumber, e);
//            conn.rollback();
//            throw e;
//        } finally {
//            try {
//                conn.setAutoCommit(originalAutoCommit);
//            } catch (SQLException e) {
//                logger.log(Level.SEVERE, "Failed to restore auto-commit state", e);
//            }
//        }
//    }
//
//}
//
