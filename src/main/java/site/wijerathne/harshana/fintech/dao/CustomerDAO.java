package site.wijerathne.harshana.fintech.dao;

import org.modelmapper.ModelMapper;
import site.wijerathne.harshana.fintech.model.Customer;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CustomerDAO {
    public List<Customer> getAllCustomers() {
        List<Customer> customers = new ArrayList<>();
        String sql = "SELECT * FROM customer";

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Customer customer = new Customer();
                customer.setId(rs.getInt("customer_id"));
                customer.setFullName(rs.getString("full_name"));
                customer.setNicPassport(rs.getString("nic_passport"));
                customer.setDob(rs.getDate("dob"));
                customer.setAddress(rs.getString("address"));
                customer.setMobile(rs.getString("mobile"));
                customer.setEmail(rs.getString("email"));
                customers.add(customer);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return customers;
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

    public Customer getCustomerById(int i) {
        return null;
    }

    public List<Customer> findCustomersByName(String name) {
        return null;
    }

    public List<Customer> findCustomersByNameOrNIC(String searchTerm) {
        List<Customer> customers = new ArrayList<>();
        String sql = "SELECT * FROM customer WHERE full_name LIKE ? OR nic_passport LIKE ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            // Add wildcards for partial matching
            String searchPattern = "%" + searchTerm + "%";
            pstmt.setString(1, searchPattern);
            pstmt.setString(2, searchPattern);

            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                Customer customer = mapResultSetToCustomer(rs);
                customers.add(customer);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return customers;
    }

    private Customer mapResultSetToCustomer(ResultSet rs) throws SQLException {
        Customer customer = new Customer();
        customer.setId(rs.getInt("customer_id"));
        customer.setFullName(rs.getString("full_name"));
        customer.setNicPassport(rs.getString("nic_passport"));
        customer.setDob(rs.getDate("dob"));
        customer.setAddress(rs.getString("address"));
        customer.setMobile(rs.getString("mobile"));
        customer.setEmail(rs.getString("email"));
        return customer;
    }
}
