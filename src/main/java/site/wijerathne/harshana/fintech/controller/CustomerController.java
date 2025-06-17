package site.wijerathne.harshana.fintech.controller;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import com.mysql.cj.exceptions.DataReadException;
import site.wijerathne.harshana.fintech.dao.CustomerDAO;
import site.wijerathne.harshana.fintech.model.Customer;
import site.wijerathne.harshana.fintech.util.SqlDateTypeAdapter;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.Date;
import java.sql.SQLException;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;


@WebServlet("/admin/customers/*")
public class CustomerController extends HttpServlet {
    private static final Logger logger = Logger.getLogger(CustomerController.class.getName());
    private CustomerDAO customerDAO;
    private Gson gson;

    @Override
    public void init() throws ServletException {
        try {
            this.customerDAO = new CustomerDAO();
            this.gson = new GsonBuilder()
                    .registerTypeAdapter(Date.class, new SqlDateTypeAdapter())
                    .create();
            logger.log(Level.INFO, "CustomerController initialized successfully");
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Controller initialization failed", e);
            throw new ServletException("Controller initialization failed", e);
        }
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");

        try {
            String pathInfo = req.getPathInfo();
            String id = req.getParameter("id");
            String search = req.getParameter("search");

            if (pathInfo != null && pathInfo.length() > 1) {
                handleGetByPath(pathInfo, resp);
            } else if (id != null) {
                handleGetById(id, resp);
            } else if (search != null && !search.trim().isEmpty()) {
                handleSearch(search, resp);
            } else {
                handleGetAll(req, resp);
            }
        } catch (NumberFormatException e) {
            logger.log(Level.WARNING, "Invalid ID format", e);
            sendError(resp, HttpServletResponse.SC_BAD_REQUEST, "Invalid ID format");
        } catch (DataReadException e) {
            logger.log(Level.SEVERE, "Database error", e);
            sendError(resp, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Database error");
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Unexpected error", e);
            sendError(resp, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Unexpected error");
        }
    }

    private void handleGetByPath(String pathInfo, HttpServletResponse resp) throws IOException {
        try {
            int id = Integer.parseInt(pathInfo.substring(1));
            Customer customer = customerDAO.getCustomerById(id);
            if (customer == null) {
                sendError(resp, HttpServletResponse.SC_NOT_FOUND, "Customer not found");
            } else {
                resp.getWriter().write(gson.toJson(customer));
            }
        } catch (NumberFormatException e) {
            throw new NumberFormatException("Invalid path parameter format");
        }
    }

    private void handleGetById(String id, HttpServletResponse resp) throws IOException {
        int customerId = Integer.parseInt(id);
        Customer customer = customerDAO.getCustomerById(customerId);
        if (customer == null) {
            sendError(resp, HttpServletResponse.SC_NOT_FOUND, "Customer not found");
        } else {
            resp.getWriter().write(gson.toJson(customer));
        }
    }

    private void handleSearch(String searchTerm, HttpServletResponse resp) throws IOException {
        try {
            if (searchTerm == null || searchTerm.trim().isEmpty()) {
                logger.log(Level.WARNING, "Empty search term provided");
                sendError(resp, HttpServletResponse.SC_BAD_REQUEST, "Search term cannot be empty");
                return;
            }

            List<Customer> customers = customerDAO.findCustomersByNameOrNIC(searchTerm);
            if (customers.isEmpty()) {
                logger.log(Level.INFO, "No customers found for search term: " + searchTerm);
                resp.setStatus(HttpServletResponse.SC_OK);
                resp.getWriter().write(gson.toJson(Collections.emptyList()));
            } else {
                logger.log(Level.INFO, "Found " + customers.size() + " customers for search term: " + searchTerm);
                resp.getWriter().write(gson.toJson(customers));
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Database error during search for: " + searchTerm, e);
            sendError(resp, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Database error during search");
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Unexpected error during search for: " + searchTerm, e);
            sendError(resp, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Unexpected error during search");
        }
    }

    private void handleGetAll(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        int page = 1;
        int pageSize = 8;

        try {
            if (req.getParameter("page") != null) {
                page = Integer.parseInt(req.getParameter("page"));
            }
            if (req.getParameter("pageSize") != null) {
                pageSize = Integer.parseInt(req.getParameter("pageSize"));
            }
        } catch (NumberFormatException e) {
            logger.log(Level.WARNING,"Invalid pagination parameters", e);
            sendError(resp, HttpServletResponse.SC_BAD_REQUEST, "Invalid pagination parameters");
            return;
        }

        List<Customer> customers = customerDAO.getAllCustomers(page, pageSize);
        resp.getWriter().write(gson.toJson(customers));
    }

    private void sendError(HttpServletResponse resp, int statusCode, String message) throws IOException {
        resp.setStatus(statusCode);
        resp.getWriter().write(gson.toJson(Map.of("error", true, "message", message)));
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");

        try {
            Customer customer = gson.fromJson(req.getReader(), Customer.class);



            if (customer.getFullName() == null || customer.getFullName().trim().isEmpty() ||
                    customer.getNicPassport() == null || customer.getNicPassport().trim().isEmpty()) {
                sendError(resp, HttpServletResponse.SC_BAD_REQUEST,
                        "Full name and NIC/Passport are required");
                return;
            }

            // Save the customer
            Customer savedCustomer = customerDAO.saveCustomer(customer);

            // Return the saved customer with generated ID
            resp.setStatus(HttpServletResponse.SC_CREATED);
            resp.getWriter().write(gson.toJson(savedCustomer));
            logger.log(Level.INFO, "Created new customer with ID: " + savedCustomer.getCustomerId());

        } catch (JsonSyntaxException e) {
            logger.log(Level.WARNING, "Invalid JSON format", e);
            sendError(resp, HttpServletResponse.SC_BAD_REQUEST, "Invalid customer data format");
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Database error while saving customer", e);
            sendError(resp, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Failed to save customer");
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Unexpected error while saving customer", e);
            sendError(resp, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Unexpected error");
        }
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        sendError(resp, HttpServletResponse.SC_METHOD_NOT_ALLOWED, "Method not supported");
    }
}
