package site.wijerathne.harshana.fintech.controller;

import com.google.gson.Gson;
import site.wijerathne.harshana.fintech.dao.CustomerDAO;
import site.wijerathne.harshana.fintech.model.Customer;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@WebServlet("/admin/customers")
public class CustomerController extends HttpServlet {

    private CustomerDAO customerDAO;
    private Gson gson;

    public void init() {
        this.customerDAO = new CustomerDAO();
        this.gson = new Gson();
    }



    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        List<Customer> customers = customerDAO.getAllCustomers();
        resp.getWriter().write(gson.toJson(customers));
    }
}
