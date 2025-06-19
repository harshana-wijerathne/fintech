package site.wijerathne.harshana.fintech.controller;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.zaxxer.hikari.HikariDataSource;
import site.wijerathne.harshana.fintech.dto.SavingAccountDTO;
import site.wijerathne.harshana.fintech.model.SavingAccount;
import site.wijerathne.harshana.fintech.service.SavingAccountService;
import site.wijerathne.harshana.fintech.service.SavingAccountServiceImpl;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.Connection;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

@WebServlet("/admin/saving-accounts/*")
public class SavingAccountController extends HttpServlet {
    private static final Logger logger = Logger.getLogger(SavingAccountController.class.getName());
    private SavingAccountService savingAccountService;
    private Gson gson;

    @Override
    public void init() throws ServletException {
        try {
            this.savingAccountService = new SavingAccountServiceImpl();
            this.gson = new GsonBuilder().create();
            logger.info("SavingAccountController initialized successfully");
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Failed to initialize SavingAccountController", e);
            throw new ServletException(e);
        }
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");

        String pathInfo = req.getPathInfo();

        HikariDataSource cp = (HikariDataSource) getServletContext().getAttribute("DATA_SOURCE");
        try (Connection connection = cp.getConnection()) {
            if (pathInfo != null && pathInfo.length() > 1) {
                String accountNumber = pathInfo.substring(1);
                SavingAccount account = savingAccountService.getSavingAccountById(accountNumber, connection);
                if (account != null) {
                    resp.getWriter().write(gson.toJson(account));
                } else {
                    resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
                    resp.getWriter().write("{\"message\":\"Account not found\"}");
                }
            } else {
                List<SavingAccount> accounts = savingAccountService.getAllSavingAccounts(connection);
                resp.getWriter().write(gson.toJson(accounts));
            }
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error fetching saving account(s)", e);
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            resp.getWriter().write("{\"message\":\"Internal error\"}");
        }
    }
}

