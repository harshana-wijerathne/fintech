package site.wijerathne.harshana.fintech.dao;


import com.mysql.cj.exceptions.DataReadException;
import site.wijerathne.harshana.fintech.model.Customer;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

public class CustomerDAO {
    private static final Logger logger = Logger.getLogger(CustomerDAO.class.getName());

    public List<Customer> getAllCustomers(int page, int pageSize) {
        if (page < 1 || pageSize < 1) {
            throw new IllegalArgumentException("Page and pageSize must be positive integers");
        }

        List<Customer> customers = new ArrayList<>();
        String sql = "SELECT * " +
                "FROM customers ORDER BY full_name LIMIT ? OFFSET ?";

        Connection conn = null;
        try {
            conn = DatabaseConnection.getConnection();
            conn.setAutoCommit(false);  // Start transaction

            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setInt(1, pageSize);
                stmt.setInt(2, (page - 1) * pageSize);

                logger.info("Executing query: " + sql + " with page=" + page + ", pageSize=" + pageSize);

                try (ResultSet rs = stmt.executeQuery()) {
                    while (rs.next()) {
                        Customer customer = new Customer();
                        customer.setCustomerId(rs.getString("customer_id"));
                        customer.setFullName(rs.getString("full_name"));
                        customer.setNicPassport(rs.getString("nic_passport"));

                        Date dob = rs.getDate("dob");
                        customer.setDob(rs.wasNull() ? null : dob);

                        String address = rs.getString("address");
                        customer.setAddress(rs.wasNull() ? null : address);

                        customer.setMobile(rs.getString("mobile_no"));
                        customer.setEmail(rs.getString("email"));
                        customer.setCreatedAt(rs.getTimestamp("created_at"));
                        customer.setUpdatedAt(rs.getTimestamp("updated_at"));

                        customers.add(customer);
                    }
                }

                conn.commit();
                logger.info("Successfully retrieved " + customers.size() + " customers");
            } catch (SQLException e) {
                if (conn != null) conn.rollback();
                throw e;
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE,"Error fetching customers from database", e);
            throw new DataReadException(e);
        } finally {
            if (conn != null) {
                try { conn.setAutoCommit(true); } catch (SQLException e) {}
                try { conn.close(); } catch (SQLException e) {}
            }
        }
        return customers;
    }

    public Customer getCustomerById(String customerId) {
        String sql = "SELECT * FROM customers WHERE customer_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, customerId);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    Customer customer = new Customer();
                    customer.setCustomerId(rs.getString("customer_id"));
                    customer.setFullName(rs.getString("full_name"));
                    customer.setNicPassport(rs.getString("nic_passport"));
                    customer.setDob(rs.getDate("dob"));
                    customer.setAddress(rs.getString("address"));
                    customer.setMobile(rs.getString("mobile_no"));
                    customer.setEmail(rs.getString("email"));
                    customer.setCreatedAt(rs.getTimestamp("created_at"));
                    customer.setUpdatedAt(rs.getTimestamp("updated_at"));
                    return customer;
                }
            }

        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error fetching customer by ID", e);
            throw new DataReadException(e);
        }
        return null;
    }

    public boolean deleteCustomer(int customerId) {
        String sql = "DELETE FROM customer WHERE customer_id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, customerId);
            int affectedRows = pstmt.executeUpdate();

            return affectedRows > 0; // Returns true if at least one row was deleted

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }


    public List<Customer> findCustomersByNameOrNIC(String searchTerm) throws SQLException {
        List<Customer> customers = new ArrayList<>();
        String sql = "SELECT customer_id, full_name, nic_passport, dob, address, mobile_no, email, created_at " +
                "FROM customers WHERE full_name LIKE ? OR nic_passport LIKE ? " +
                "LIMIT 100";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            // Validate and prepare search term
            if (searchTerm == null || searchTerm.trim().isEmpty()) {
                throw new IllegalArgumentException("Search term cannot be empty");
            }

            String sanitizedTerm = sanitizeSearchTerm(searchTerm);
            String searchPattern = "%" + sanitizedTerm + "%";

            pstmt.setString(1, searchPattern);
            pstmt.setString(2, searchPattern);

            logger.log(Level.INFO, "Executing search query for term: " + sanitizedTerm);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Customer customer = new Customer();
                    // Note: Using getString for UUID since it's CHAR(36)
                    customer.setCustomerId(rs.getString("customer_id"));
                    customer.setFullName(rs.getString("full_name"));
                    customer.setNicPassport(rs.getString("nic_passport"));

                    // Handle required NOT NULL fields
                    customer.setDob(rs.getDate("dob"));
                    customer.setAddress(rs.getString("address"));
                    customer.setMobile(rs.getString("mobile_no"));  // Changed from mobile to mobile_no

                    // Handle optional email
                    String email = rs.getString("email");
                    customer.setEmail(rs.wasNull() ? null : email);

                    // Add timestamp if needed
                    // customer.setCreatedAt(rs.getTimestamp("created_at"));

                    customers.add(customer);
                }
            }

            logger.log(Level.INFO, "Found " + customers.size() + " results for search term: " + sanitizedTerm);
            return customers;

        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Database error during search for term: " + searchTerm, e);
            throw new SQLException("Search failed", e);
        }
    }

    private String sanitizeSearchTerm(String term) {
        if (term == null) return "";
        // Remove wildcards to prevent overly broad searches
        return term.replaceAll("[%_\\\\]", "");
    }


    public Customer saveCustomer(Customer customer) throws SQLException {
        // SQL matches exact table structure with all columns
        String sql = "INSERT INTO customers (customer_id, nic_passport, full_name, dob, address, mobile_no, email, created_at, updated_at) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";

        // Generate values that will be used in the insert
        String customerId = UUID.randomUUID().toString();
        Timestamp now = new Timestamp(System.currentTimeMillis());

        try (Connection conn = DatabaseConnection.getConnection()) {
            conn.setAutoCommit(false); // Start transaction

            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                // Set all parameters according to table structure
                pstmt.setString(1, customerId);
                pstmt.setString(2, customer.getNicPassport());
                pstmt.setString(3, customer.getFullName());
                pstmt.setDate(4, new java.sql.Date(customer.getDob().getTime()));
                pstmt.setString(5, customer.getAddress());
                pstmt.setString(6, customer.getMobile()); // Maps to mobile_no in DB
                pstmt.setString(7, customer.getEmail());
                pstmt.setTimestamp(8, now); // created_at
                pstmt.setTimestamp(9, now); // updated_at

                // Execute and check affected rows
                int affectedRows = pstmt.executeUpdate();
                if (affectedRows == 0) {
                    throw new SQLException("Creating customer failed, no rows affected");
                }

                // Set generated values back to customer object
                customer.setCustomerId(customerId);
                customer.setCreatedAt(now);
                customer.setUpdatedAt(now);

                conn.commit(); // Commit transaction
                logger.log(Level.INFO, "Saved new customer with ID: {}", customerId);

                return customer;
            } catch (SQLException e) {
                conn.rollback(); // Rollback on error
                logger.log(Level.SEVERE, "Error saving customer: {}"+e.getMessage(), e);
                throw new SQLException("Error saving customer: " + e.getMessage(), e);
            } finally {
                conn.setAutoCommit(true); // Reset auto-commit
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Database connection error: {}"+ e.getMessage(), e);
            throw new SQLException("Database connection error", e);
        }
    }
}
