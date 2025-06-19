package site.wijerathne.harshana.fintech.controller;

import com.zaxxer.hikari.HikariDataSource;
import site.wijerathne.harshana.fintech.repo.AuditLogRepo;
import site.wijerathne.harshana.fintech.dto.AuditLogDTO;
import site.wijerathne.harshana.fintech.model.User;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;
import java.sql.SQLException;

@WebServlet("/logout")
public class LogoutController extends HttpServlet {

    private final AuditLogRepo auditLogRepo = new AuditLogRepo();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws  IOException {
        HttpSession session = req.getSession(false);
        HikariDataSource dataSource = (HikariDataSource) getServletContext().getAttribute("DATA_SOURCE");
        if (session != null) {
            User user = (User) session.getAttribute("username");

            if (user != null) {
                AuditLogDTO log = new AuditLogDTO(
                        user.getUserId(),
                        "LOGOUT",
                        "USER",
                        null,
                        "User logged out",
                        req.getRemoteAddr()
                );
                try {
                    auditLogRepo.saveAuditLog(log,dataSource.getConnection());
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            }

            session.invalidate();
        }

        resp.sendRedirect("pages/login.jsp");
    }
}
