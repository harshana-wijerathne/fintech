package site.wijerathne.harshana.fintech.controller;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import com.zaxxer.hikari.HikariDataSource;
import site.wijerathne.harshana.fintech.dto.account.AccountDetailsRequestDTO;
import site.wijerathne.harshana.fintech.dto.account.AccountDetailsResponseDTO;
import site.wijerathne.harshana.fintech.dto.account.AccountRequestDTO;
import site.wijerathne.harshana.fintech.exception.EntityNotFoundException;
import site.wijerathne.harshana.fintech.exception.account.*;
import site.wijerathne.harshana.fintech.repo.AuditLogRepo;
import site.wijerathne.harshana.fintech.repo.account.AccountRepo;
import site.wijerathne.harshana.fintech.repo.account.AccountRepoImpl;
import site.wijerathne.harshana.fintech.repo.customer.CustomerRepo;
import site.wijerathne.harshana.fintech.service.account.AccountService;
import site.wijerathne.harshana.fintech.service.account.AccountServiceImpl;
import site.wijerathne.harshana.fintech.util.Page;
import site.wijerathne.harshana.fintech.util.SqlDateTypeAdapter;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.sql.Date;
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
            this.gson = new GsonBuilder()
                    .registerTypeAdapter(Date.class, new SqlDateTypeAdapter())
                    .create();
            HikariDataSource dataSource = (HikariDataSource) getServletContext().getAttribute("DATA_SOURCE");
            AccountRepo accountRepo = new AccountRepoImpl(dataSource);
            CustomerRepo customerRepo = new CustomerRepo(dataSource);
            AuditLogRepo auditLogRepo = new AuditLogRepo(dataSource);
            this.accountService = new AccountServiceImpl(accountRepo, customerRepo, auditLogRepo);
            logger.info("AccountController initialized successfully");
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Failed to initialize AccountController", e);
            throw new ServletException("AccountController initialization failed", e);
        }
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");

        try {
            String pathInfo = req.getPathInfo();
            String pageSize = req.getParameter("pageSize");
            String page = req.getParameter("page");
            String key = req.getParameter("key");

            if ((pathInfo == null || pathInfo.equals("/")) && (req.getQueryString() == null ||
                    req.getQueryString().contains("page=") || req.getQueryString().contains("pageSize="))) {
                handleGetAllAccounts(page, pageSize, resp);
            } else if (pathInfo != null && pathInfo.matches("/\\d+")) {
                handleGetAccountByNumber(pathInfo.substring(1), resp);
            } else if (key != null) {
                handleSearchAccounts(key, resp);
            } else {
                sendError(resp, HttpServletResponse.SC_NOT_FOUND, "Invalid endpoint");
            }
        } catch (NumberFormatException e) {
            logger.log(Level.WARNING, "Invalid number format in request", e);
            sendError(resp, HttpServletResponse.SC_BAD_REQUEST, "Invalid number format");
        } catch (AccountNotFoundException e) {
            logger.log(Level.WARNING, e.getMessage());
            sendError(resp, HttpServletResponse.SC_NOT_FOUND, e.getMessage());
        } catch (IllegalArgumentException e) {
            logger.log(Level.WARNING, e.getMessage());
            sendError(resp, HttpServletResponse.SC_BAD_REQUEST, e.getMessage());
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Unexpected error processing GET request", e);
            sendError(resp, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Internal server error");
        }
    }

    private void handleGetAllAccounts(String page, String pageSize, HttpServletResponse resp) throws IOException {
        try {
            Page<AccountDetailsResponseDTO> allAccounts = accountService.getAllAccounts(page, pageSize);
            resp.getWriter().write(gson.toJson(allAccounts));
        } catch (IllegalArgumentException e) {
            logger.log(Level.WARNING, "Invalid pagination parameters", e);
            sendError(resp, HttpServletResponse.SC_BAD_REQUEST, e.getMessage());
        } catch (AccountServiceException e) {
            logger.log(Level.SEVERE, "Error fetching accounts", e);
            sendError(resp, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Error fetching accounts");
        }
    }

    private void handleGetAccountByNumber(String accountNumber, HttpServletResponse resp) throws IOException {
        try {
            AccountDetailsResponseDTO accountDetails = accountService.getAccountByAccountNumber(accountNumber);
            resp.getWriter().write(gson.toJson(accountDetails));
        } catch (AccountNotFoundException e) {
            throw e;
        } catch (IllegalArgumentException e) {
            logger.log(Level.WARNING, "Invalid account number format", e);
            sendError(resp, HttpServletResponse.SC_BAD_REQUEST, "Invalid account number format");
        } catch (AccountServiceException e) {
            logger.log(Level.SEVERE, "Error fetching account", e);
            sendError(resp, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Error fetching account details");
        }
    }

    private void handleSearchAccounts(String key, HttpServletResponse resp) throws IOException {
        try {
            List<AccountDetailsResponseDTO> accounts = accountService.searchAccount(key);
            resp.getWriter().write(gson.toJson(accounts));
        } catch (IllegalArgumentException e) {
            logger.log(Level.WARNING, "Invalid search parameter", e);
            sendError(resp, HttpServletResponse.SC_BAD_REQUEST, e.getMessage());
        } catch (AccountServiceException e) {
            logger.log(Level.SEVERE, "Error searching accounts", e);
            sendError(resp, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Error searching accounts");
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");

        if (!"application/json".equalsIgnoreCase(req.getContentType())) {
            sendError(resp, HttpServletResponse.SC_UNSUPPORTED_MEDIA_TYPE, "Content-Type must be application/json");
            return;
        }

        try (BufferedReader reader = req.getReader()) {
            AccountRequestDTO accountRequestDTO = gson.fromJson(reader, AccountRequestDTO.class);
            AccountDetailsResponseDTO accountDetails = accountService.saveAccount(accountRequestDTO, req);

            resp.setStatus(HttpServletResponse.SC_CREATED);
            resp.getWriter().write(gson.toJson(accountDetails));
            logger.log(Level.INFO, "Created new account: " + accountDetails.getAccountNumber());
        } catch (JsonSyntaxException e) {
            logger.log(Level.WARNING, "Invalid JSON format", e);
            sendError(resp, HttpServletResponse.SC_BAD_REQUEST, "Invalid account data format");
        } catch (EntityNotFoundException e) {
            logger.log(Level.WARNING, e.getMessage());
            sendError(resp, HttpServletResponse.SC_NOT_FOUND, e.getMessage());
        } catch (IllegalArgumentException e) {
            logger.log(Level.WARNING, "Validation error: " + e.getMessage(), e);
            sendError(resp, HttpServletResponse.SC_BAD_REQUEST, e.getMessage());
        } catch (AccountCreationException e) {
            logger.log(Level.SEVERE, "Error creating account", e);
            sendError(resp, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Failed to create account");
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Unexpected error creating account", e);
            sendError(resp, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Internal server error");
        }
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");

        String pathInfo = req.getPathInfo();
        if (pathInfo == null || pathInfo.isBlank()) {
            sendError(resp, HttpServletResponse.SC_BAD_REQUEST, "Account number must be provided in the path");
            return;
        }

        try {
            String accountNumber = pathInfo.substring(1);
            boolean deleted = accountService.deleteAccountById(accountNumber, req);

            if (deleted) {
                resp.getWriter().write(gson.toJson(Map.of(
                        "success", true,
                        "message", "Account deleted successfully"
                )));
                resp.setStatus(HttpServletResponse.SC_OK);
                logger.log(Level.INFO, "Deleted account: " + accountNumber);
            }
        } catch (AccountNotFoundException e) {
            logger.log(Level.WARNING, e.getMessage());
            sendError(resp, HttpServletResponse.SC_NOT_FOUND, e.getMessage());
        } catch (IllegalArgumentException e) {
            logger.log(Level.WARNING, "Invalid account number format", e);
            sendError(resp, HttpServletResponse.SC_BAD_REQUEST, e.getMessage());
        } catch (AccountDeletionException e) {
            logger.log(Level.SEVERE, "Error deleting account", e);
            sendError(resp, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Failed to delete account");
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Unexpected error deleting account", e);
            sendError(resp, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Internal server error");
        }
    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");

        if (!"application/json".equalsIgnoreCase(req.getContentType())) {
            sendError(resp, HttpServletResponse.SC_UNSUPPORTED_MEDIA_TYPE, "Content-Type must be application/json");
            return;
        }

        try (BufferedReader reader = req.getReader()) {
            AccountDetailsRequestDTO dto = gson.fromJson(reader, AccountDetailsRequestDTO.class);
            AccountDetailsResponseDTO updated = accountService.updateAccountDetails(dto, req);

            resp.setStatus(HttpServletResponse.SC_OK);
            resp.getWriter().write(gson.toJson(updated));
            logger.log(Level.INFO, "Updated account: " + updated.getAccountNumber());
        } catch (JsonSyntaxException e) {
            logger.log(Level.WARNING, "Invalid JSON format", e);
            sendError(resp, HttpServletResponse.SC_BAD_REQUEST, "Invalid account data format");
        } catch (AccountNotFoundException | EntityNotFoundException e) {
            logger.log(Level.WARNING, e.getMessage());
            sendError(resp, HttpServletResponse.SC_NOT_FOUND, e.getMessage());
        } catch (IllegalArgumentException e) {
            logger.log(Level.WARNING, "Validation error: " + e.getMessage(), e);
            sendError(resp, HttpServletResponse.SC_BAD_REQUEST, e.getMessage());
        } catch (AccountUpdateException e) {
            logger.log(Level.SEVERE, "Error updating account", e);
            sendError(resp, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Failed to update account");
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Unexpected error updating account", e);
            sendError(resp, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Internal server error");
        }
    }

    private void sendError(HttpServletResponse resp, int statusCode, String message) throws IOException {
        resp.setStatus(statusCode);
        resp.getWriter().write(gson.toJson(Map.of("error", true, "message", message)));
    }
}