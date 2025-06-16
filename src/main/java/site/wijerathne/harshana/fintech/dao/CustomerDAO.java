package site.wijerathne.harshana.fintech.dao;

import org.modelmapper.ModelMapper;
import site.wijerathne.harshana.fintech.model.Customer;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
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
}
