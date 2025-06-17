package site.wijerathne.harshana.fintech.controller;

import com.google.gson.Gson;
import site.wijerathne.harshana.fintech.dao.CustomerDAO;
import site.wijerathne.harshana.fintech.model.Customer;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@WebServlet("/admin/customers/*")
public class CustomerController extends HttpServlet {

    private CustomerDAO customerDAO;
    private Gson gson;

    public void init() {
        this.customerDAO = new CustomerDAO();
        this.gson = new Gson();
    }



    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("application/json");
        String id = req.getParameter("id");
        String name = req.getParameter("name");
        String search = req.getParameter("search");

        try {
            if (id != null) {
                Customer customer = customerDAO.getCustomerById(Integer.parseInt(id));
                resp.getWriter().write(gson.toJson(customer));
            } else if (search != null) {
                // Handle GET /api/customers?name=John
                List<Customer> customers2 = customerDAO.findCustomersByNameOrNIC(search);
                resp.setContentType("application/json");
                resp.getWriter().write(gson.toJson(customers2));
            } else {
                List<Customer> customers = customerDAO.getAllCustomers();
                resp.setContentType("application/json");
                resp.getWriter().write(gson.toJson(customers));
            }
        } catch (Exception e) {
            resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());
        }

    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String id = req.getParameter("id");
        if (id != null) {
            boolean b = customerDAO.deleteCustomer(Integer.parseInt(id));
            if (b) {
                resp.setStatus(HttpServletResponse.SC_NO_CONTENT);
            }else {
                resp.sendError(HttpServletResponse.SC_NOT_FOUND, "Customer not found");
            }
        }else {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST);
        }

    }
}
