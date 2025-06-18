package site.wijerathne.harshana.fintech.dao;


import org.junit.jupiter.api.*;
import site.wijerathne.harshana.fintech.model.Customer;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.*;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;


public class CustomerDAOTest {

    Connection connection;

    @BeforeEach
    void setUp() throws ClassNotFoundException, SQLException {
        Class.forName("org.h2.Driver");
        connection = DriverManager.getConnection(
                "jdbc:h2:mem:test;MODE=MYSQL;DATABASE_TO_LOWER=TRUE;DEFAULT_NULL_ORDERING=HIGH",
                "","");

        executeScripts();

    }

    private void executeScripts() {
        try (BufferedReader schemeBr = new BufferedReader(new InputStreamReader
                (Objects.requireNonNull(getClass().getResourceAsStream("/schema.sql"))))) {
            StringBuilder schemaScript = new StringBuilder();
            schemeBr.lines().forEach(schemaScript::append);
            try (var stm = connection.createStatement()) {
                stm.execute(schemaScript.toString());
            }

            Path path = Path.of(Objects.requireNonNull(getClass().getResource("/data.sql")).toURI());
            String dataScript = Files.readString(path);
            try (var stm = connection.createStatement()) {
                stm.execute(dataScript);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    @AfterEach
    void tearDown() throws SQLException {
        connection.close();
    }

    @Test
    void getAllCustomers() {
        // 2. Exercise (SUT = System Under Test)
        List<Customer> customerList = CustomerDAO.getAllCustomers(1,8);

        // 3. Verify (State)
        assertFalse(customerList.isEmpty());
        assertEquals(8, customerList.size());
        customerList.forEach(System.out::println);
    }

    @Test
    void testSaveCustomer() throws SQLException {
        // Prepare a dummy customer
        Customer customer = new Customer();
        customer.setNicPassport(UUID.randomUUID().toString());
        customer.setFullName("Harshana Wijerathne");
        customer.setDob(new Date(100000));
        customer.setAddress("Colombo");
        customer.setMobile("0771234567");
        customer.setEmail("test@example.com");

        // Save customer
        CustomerDAO repo = new CustomerDAO();
        Customer savedCustomer = repo.saveCustomer(customer);

        // Assertions
        assertNotNull(savedCustomer.getCustomerId());
        assertEquals("Harshana Wijerathne", savedCustomer.getFullName());
        assertNotNull(savedCustomer.getCreatedAt());

        // Also query the database directly
        try (PreparedStatement pstmt = connection.prepareStatement(
                "SELECT * FROM customers WHERE customer_id = ?")) {
            pstmt.setString(1, savedCustomer.getCustomerId());
            ResultSet rs = pstmt.executeQuery();
//            assertTrue(rs.next());
            assertEquals("Harshana Wijerathne", rs.getString("full_name"));
            assertEquals("0771234567", rs.getString("mobile_no"));
        }
    }

}



