package site.wijerathne.harshana.fintech.controller;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.zaxxer.hikari.HikariDataSource;
import site.wijerathne.harshana.fintech.dto.account.AccountDetailsResponseDTO;
import site.wijerathne.harshana.fintech.dto.customer.CustomerDTO;
import site.wijerathne.harshana.fintech.model.Account;
import site.wijerathne.harshana.fintech.repo.account.AccountRepo;
import site.wijerathne.harshana.fintech.repo.account.AccountRepoImpl;
import site.wijerathne.harshana.fintech.service.account.AccountService;
import site.wijerathne.harshana.fintech.service.account.AccountServiceImpl;
import site.wijerathne.harshana.fintech.util.Page;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.Connection;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

@WebServlet("/admin/saving-accounts/*")
public class AccountController extends HttpServlet {
    private static final Logger logger = Logger.getLogger(AccountController.class.getName());
    private AccountService accountService;
    private Gson gson;

    @Override
    public void init() throws ServletException {
        try {

            this.gson = new GsonBuilder().create();
            HikariDataSource dataSource = (HikariDataSource) getServletContext().getAttribute("DATA_SOURCE");
            AccountRepo accountRepo = new AccountRepoImpl(dataSource);
            this.accountService = new AccountServiceImpl(accountRepo);
            logger.info("SavingAccountController initialized successfully");
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Failed to initialize SavingAccountController", e);
            throw new ServletException(e);
        }
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("application/json");

        try {
            String pathInfo = req.getPathInfo();
            String queryString = req.getQueryString();
            String pageSize = req.getParameter("pageSize");
            String page = req.getParameter("page");
            String key = req.getParameter("key");

            if ((pathInfo == null || pathInfo.equals("/")) &&(queryString == null || queryString.contains("page=") ||queryString.contains("pageSize=") )) {
                try {
                    Page<AccountDetailsResponseDTO> allAccounts = accountService.getAllAccounts(page , pageSize);
                    resp.getWriter().write(gson.toJson(allAccounts));
                }catch (Exception e){
                    logger.log(Level.WARNING, "Error while getting all accounts", e);
                    resp.sendError(HttpServletResponse.SC_BAD_REQUEST , "invalid request");
                }
            } else if (pathInfo != null && pathInfo.matches("/\\d+")) {
                try {
                    AccountDetailsResponseDTO accountDetails = accountService.getAccountById(pathInfo.substring(1));
                    resp.getWriter().write(gson.toJson(accountDetails));
                }catch (Exception e){
                    resp.sendError(HttpServletResponse.SC_BAD_REQUEST , "Account not found");
                }

            } else if (queryString != null && queryString.contains("key=")) {
                try{
                    List<AccountDetailsResponseDTO> accountDetailsResponseDTOS = accountService.searchAccount(key);
                    resp.getWriter().write(gson.toJson(accountDetailsResponseDTOS));
                }catch (Exception e){
                    resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR , "Internal server error");
                }
            } else {
                resp.sendError(HttpServletResponse.SC_NOT_FOUND, "Invalid endpoint");
            }
        } catch (NumberFormatException e) {
            logger.log(Level.WARNING, "Invalid ID format in request", e);
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid customer ID format");
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Unexpected error processing GET request", e);
            resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Server error");
        }

    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("application/json");
        HikariDataSource cp = (HikariDataSource) getServletContext().getAttribute("DATA_SOURCE");

        try(Connection connection = cp.getConnection()){
            Account account = gson.fromJson(req.getReader(), Account.class);
        }catch (Exception e){
            logger.log(Level.SEVERE, "Failed to fetch saving account", e);
        }
    }

    private void sendError(HttpServletResponse resp, int statusCode, String message) throws IOException {
        resp.setStatus(statusCode);
        resp.getWriter().write(gson.toJson(Map.of("error", true, "message", message)));
    }
}

