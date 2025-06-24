package site.wijerathne.harshana.fintech.controller;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.zaxxer.hikari.HikariDataSource;
import lombok.extern.slf4j.Slf4j;
import site.wijerathne.harshana.fintech.dto.transaction.TransactionRequestDTO;
import site.wijerathne.harshana.fintech.dto.transaction.TransactionResponseDTO;
import site.wijerathne.harshana.fintech.exception.transaction.AccountNotFoundException;
import site.wijerathne.harshana.fintech.exception.transaction.InsufficientFundsException;
import site.wijerathne.harshana.fintech.exception.transaction.InvalidDateRangeException;
import site.wijerathne.harshana.fintech.exception.transaction.InvalidTransactionException;
import site.wijerathne.harshana.fintech.repo.AuditLogRepo;
import site.wijerathne.harshana.fintech.repo.transaction.TransactionRepo;
import site.wijerathne.harshana.fintech.repo.transaction.TransactionRepoImpl;
import site.wijerathne.harshana.fintech.service.transaction.TransactionService;
import site.wijerathne.harshana.fintech.service.transaction.TransactionServiceImpl;
import site.wijerathne.harshana.fintech.util.Page;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.math.BigDecimal;
import java.sql.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

@Slf4j
@WebServlet("/admin/transactions/*")
public class TransactionController extends HttpServlet {
    private static final Logger logger = Logger.getLogger(TransactionController.class.getName());
    private TransactionService transactionService;
    private Gson gson;
    private AuditLogRepo auditLogger;

    @Override
    public void init() throws ServletException {
        try {
            this.gson = new Gson();
            HikariDataSource connectionPool = (HikariDataSource) getServletContext().getAttribute("DATA_SOURCE");
            AuditLogRepo auditLogger = new AuditLogRepo(connectionPool);
            TransactionRepo transactionRepo = new TransactionRepoImpl(connectionPool);
            this.transactionService = new TransactionServiceImpl(transactionRepo,auditLogger);
            logger.info("TransactionServlet initialized successfully");
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Failed to initialize TransactionServlet", e);
            throw new ServletException("Failed to initialize TransactionServlet", e);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");

        try {
            TransactionRequestDTO request = parseRequest(req);
            validateTransactionRequest(request);
            TransactionResponseDTO response = transactionService.deposit(request , req);
            resp.setStatus(HttpServletResponse.SC_OK);
            resp.getWriter().write(gson.toJson(response));
            logger.log(Level.INFO, "Deposit processed successfully for account: " + request.getAccountNumber());

        } catch (JsonSyntaxException e) {
            logger.log(Level.WARNING, "Invalid JSON format in request", e);
            sendError(resp, HttpServletResponse.SC_BAD_REQUEST, "Invalid request format");
        } catch (InvalidTransactionException e) {
            logger.log(Level.WARNING, "Invalid deposit request: " + e.getMessage());
            sendError(resp, HttpServletResponse.SC_BAD_REQUEST, e.getMessage());
        } catch (AccountNotFoundException e) {
            logger.log(Level.WARNING, "Account not found: " + e.getMessage());
            sendError(resp, HttpServletResponse.SC_NOT_FOUND, e.getMessage());
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Deposit processing failed", e);
            sendError(resp, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Deposit processing failed");
        }
    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");

        try {
            TransactionRequestDTO request = parseRequest(req);

            validateTransactionRequest(request);

            TransactionResponseDTO response = transactionService.withdraw(request);

            resp.setStatus(HttpServletResponse.SC_OK);
            resp.getWriter().write(gson.toJson(response));
            logger.log(Level.INFO, "Withdrawal processed successfully for account: " + request.getAccountNumber());

        } catch (JsonSyntaxException e) {
            logger.log(Level.WARNING, "Invalid JSON format in request", e);
            sendError(resp, HttpServletResponse.SC_BAD_REQUEST, "Invalid request format");
        } catch (InvalidTransactionException e) {
            logger.log(Level.WARNING, "Invalid withdrawal request: " + e.getMessage());
            sendError(resp, HttpServletResponse.SC_BAD_REQUEST, e.getMessage());
        } catch (AccountNotFoundException e) {
            logger.log(Level.WARNING, "Account not found: " + e.getMessage());
            sendError(resp, HttpServletResponse.SC_NOT_FOUND, e.getMessage());
        } catch (InsufficientFundsException e) {
            logger.log(Level.WARNING, "Insufficient funds: " + e.getMessage());
            sendError(resp, HttpServletResponse.SC_BAD_REQUEST, e.getMessage());
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Withdrawal processing failed", e);
            sendError(resp, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Withdrawal processing failed");
        }
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");

        try {
            String pathInfo = req.getPathInfo();
            String fromDateParam = req.getParameter("fromdate");
            String toDateParam = req.getParameter("todate");

            // Parse pagination parameters with defaults
            int page = parseIntParameter(req, "page", 1);
            int pageSize = parseIntParameter(req, "pageSize", 10);

            Page<TransactionResponseDTO> transactions;

            if (pathInfo == null || pathInfo.equals("/")) {
                if (fromDateParam != null && toDateParam != null) {
                    Date startDate = Date.valueOf(fromDateParam);
                    Date endDate = Date.valueOf(toDateParam);
                    transactions = transactionService.getTransactionsByDateRange(
                            null, startDate, endDate, page, pageSize);
                } else {
                    transactions = transactionService.getAllTransactions(page, pageSize);
                }
            } else {
                String[] pathParts = pathInfo.split("/");
                if (pathParts.length == 2) {
                    String accountNumber = pathParts[1];
                    if (fromDateParam != null && toDateParam != null) {
                        Date startDate = Date.valueOf(fromDateParam);
                        Date endDate = Date.valueOf(toDateParam);
                        transactions = transactionService.getTransactionsByDateRange(
                                accountNumber, startDate, endDate, page, pageSize);
                    } else {
                        transactions = transactionService.getTransactionsByAccountNumber(
                                accountNumber, page, pageSize);
                    }
                } else {
                    throw new IllegalArgumentException("Invalid URL pattern");
                }
            }

            resp.setStatus(HttpServletResponse.SC_OK);
            resp.getWriter().write(gson.toJson(transactions));

        } catch (AccountNotFoundException e) {
            sendError(resp, HttpServletResponse.SC_NOT_FOUND, e.getMessage());
        } catch (IllegalArgumentException | InvalidDateRangeException e) {
            sendError(resp, HttpServletResponse.SC_BAD_REQUEST, e.getMessage());
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error retrieving transactions", e);
            sendError(resp, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Error retrieving transactions");
        }
    }

    private int parseIntParameter(HttpServletRequest req, String paramName, int defaultValue) {
        String paramValue = req.getParameter(paramName);
        if (paramValue != null && !paramValue.isEmpty()) {
            try {
                return Integer.parseInt(paramValue);
            } catch (NumberFormatException e) {
                return defaultValue;
            }
        }
        return defaultValue;
    }

    private TransactionRequestDTO parseRequest(HttpServletRequest req) throws IOException {
        try (BufferedReader reader = req.getReader()) {
            System.out.println(reader);
            return gson.fromJson(reader, TransactionRequestDTO.class);
        }
    }

    private void validateTransactionRequest(TransactionRequestDTO request) throws InvalidTransactionException {
        if (request == null) {
            throw new InvalidTransactionException("Request body is required");
        }
        if (request.getAccountNumber() == null || request.getAccountNumber().trim().isEmpty()) {
            throw new InvalidTransactionException("Account number is required");
        }
        if (request.getAmount() == null || request.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new InvalidTransactionException("Amount must be positive");
        }
    }

    private void sendError(HttpServletResponse resp, int statusCode, String message) throws IOException {
        resp.setStatus(statusCode);
        Map<String, Object> errorResponse = new HashMap<>();
        errorResponse.put("error", true);
        errorResponse.put("message", message);
        resp.getWriter().write(gson.toJson(errorResponse));
    }
}