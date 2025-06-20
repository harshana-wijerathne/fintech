package site.wijerathne.harshana.fintech.repo.customer;


import com.mysql.cj.exceptions.DataReadException;
import com.zaxxer.hikari.HikariDataSource;
import site.wijerathne.harshana.fintech.exception.DataAccessException;
import site.wijerathne.harshana.fintech.model.Customer;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

public class CustomerRepo {
    private final Logger logger = Logger.getLogger(CustomerRepo.class.getName());
    private HikariDataSource connectionPool;

    public CustomerRepo(HikariDataSource connectionPool) {
        this.connectionPool = connectionPool;
    }

    public List<Customer> getAllCustomers(int page, int pageSize) {
        if (page < 1 || pageSize < 1) {
            throw new IllegalArgumentException("Page and pageSize must be positive integers");
        }

        List<Customer> customers = new ArrayList<>();
        String sql = "SELECT * " +
                "FROM customers ORDER BY full_name LIMIT ? OFFSET ?";

        try (
                Connection connection = connectionPool.getConnection();
                PreparedStatement stmt = connection.prepareStatement(sql)) {

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
                return customers;
            } catch (SQLException e) {
                logger.log(Level.SEVERE, "Error fetching customers from database", e);
                throw new DataAccessException("Error fetching customers from database", e);
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error fetching customers from database", e);
            throw new DataAccessException("Error fetching customers from database", e);
        }
    }

    public Customer getCustomerById(String customerId) {
        String sql = "SELECT * FROM customers WHERE customer_id = ?";
        try (Connection connection = connectionPool.getConnection();
             PreparedStatement pstmt = connection.prepareStatement(sql)) {

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

    public List<Customer> findCustomersByNameOrNIC(String searchTerm) throws SQLException {
        List<Customer> customers = new ArrayList<>();
        String sql = "SELECT customer_id, full_name, nic_passport, dob, address, mobile_no, email, created_at " +
                "FROM customers WHERE full_name LIKE ? OR nic_passport LIKE ? " +
                "LIMIT 100";

        try (   Connection connection = connectionPool.getConnection();
                PreparedStatement pstmt = connection.prepareStatement(sql)) {

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
                    customer.setCustomerId(rs.getString("customer_id"));
                    customer.setFullName(rs.getString("full_name"));
                    customer.setNicPassport(rs.getString("nic_passport"));

                    customer.setDob(rs.getDate("dob"));
                    customer.setAddress(rs.getString("address"));
                    customer.setMobile(rs.getString("mobile_no"));  // Changed from mobile to mobile_no

                    String email = rs.getString("email");
                    customer.setEmail(rs.wasNull() ? null : email);

                    customers.add(customer);
                }
            }

            logger.log(Level.INFO, "Found " + customers.size() + " results for search term: " + sanitizedTerm);
            return customers;

        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Database error during search for term: " + searchTerm, e);
            throw new DataAccessException("Search failed", e);
        }
    }

    public Customer saveCustomer(Customer customer) throws SQLException {

        String sql = "INSERT INTO customers (customer_id, nic_passport, full_name, dob, address, mobile_no, email, created_at, updated_at) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";


        String customerId = UUID.randomUUID().toString();
        Timestamp now = new Timestamp(System.currentTimeMillis());
        try(Connection connection = connectionPool.getConnection();) {
            connection.setAutoCommit(false);
            try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
                pstmt.setString(1, customerId);
                pstmt.setString(2, customer.getNicPassport());
                pstmt.setString(3, customer.getFullName());
                pstmt.setDate(4, new java.sql.Date(customer.getDob().getTime()));
                pstmt.setString(5, customer.getAddress());
                pstmt.setString(6, customer.getMobile());
                pstmt.setString(7, customer.getEmail());
                pstmt.setTimestamp(8, now);
                pstmt.setTimestamp(9, now);

                int affectedRows = pstmt.executeUpdate();
                if (affectedRows == 0) {
                    throw new SQLException("Creating customer failed, no rows affected");
                }
                customer.setCustomerId(customerId);
                customer.setCreatedAt(now);
                customer.setUpdatedAt(now);

                connection.commit();
                logger.log(Level.INFO, "Saved new customer with ID: {}", customerId);
                return customer;
            } catch (SQLException e) {
                connection.rollback(); // Rollback on error
                logger.log(Level.SEVERE, "Error saving customer: {}" + e.getMessage(), e);
                throw new DataAccessException("Error saving customer: " + e.getMessage(), e);
            } finally {
                connection.setAutoCommit(true);
            }
        } catch (SQLException e) {
            throw new DataAccessException("Error saving customer: " + e.getMessage(), e);
        }
    }

    public boolean deleteCustomer(String customerId) throws SQLException {
        String sql = "DELETE FROM customers WHERE customer_id = ?";

        try ( Connection connection = connectionPool.getConnection();
                PreparedStatement pstmt = connection.prepareStatement(sql)) {

            pstmt.setString(1, customerId);
            int affectedRows = pstmt.executeUpdate();

            return affectedRows > 0;
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error deleting customer with ID: " + customerId, e);
            throw e;
        }
    }

    public Customer updateCustomer(Customer customer) throws SQLException {
        String sql = "UPDATE customers SET nic_passport = ?, full_name = ?, dob = ?, " +
                "address = ?, mobile_no = ?, email = ?, updated_at = ? " +
                "WHERE customer_id = ?";

        try ( Connection conn = connectionPool.getConnection()){
            conn.setAutoCommit(false);
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                Timestamp now = new Timestamp(System.currentTimeMillis());

                pstmt.setString(1, customer.getNicPassport());
                pstmt.setString(2, customer.getFullName());
                pstmt.setDate(3, customer.getDob() != null ? new java.sql.Date(customer.getDob().getTime()) : null);
                pstmt.setString(4, customer.getAddress());
                pstmt.setString(5, customer.getMobile());
                pstmt.setString(6, customer.getEmail());
                pstmt.setTimestamp(7, now);
                pstmt.setString(8, customer.getCustomerId());

                int affectedRows = pstmt.executeUpdate();
                if (affectedRows == 0) {
                    throw new SQLException("Updating customer failed, no rows affected");
                }
                customer.setUpdatedAt(now);
                conn.commit();
                return customer;
            } catch (SQLException e) {
                conn.rollback();
                throw new DataAccessException("Error updating customer: " + e.getMessage(), e);
            }
        }catch (SQLException e){
            logger.log(Level.SEVERE, "Error updating customer: {}" + e.getMessage(), e);
            throw new DataAccessException("Error updating customer: " + e.getMessage(), e);
        }


    }

    private String sanitizeSearchTerm(String term) {
        if (term == null) return "";
        return term.replaceAll("[%_\\\\]", "");
    }


}
