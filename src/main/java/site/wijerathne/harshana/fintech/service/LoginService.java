package site.wijerathne.harshana.fintech.service;

import org.mindrot.jbcrypt.BCrypt;
import site.wijerathne.harshana.fintech.dao.AuditLogDAO;
import site.wijerathne.harshana.fintech.dao.LoginDAO;
import site.wijerathne.harshana.fintech.dto.AuditLogDTO;
import site.wijerathne.harshana.fintech.dto.LoginRequestDTO;
import site.wijerathne.harshana.fintech.model.User;

import javax.servlet.http.HttpServletRequest;

public class LoginService {

    private final LoginDAO loginDAO = new LoginDAO();
    private final AuditLogDAO auditLogDAO = new AuditLogDAO();

    public User authenticate(LoginRequestDTO loginDTO, HttpServletRequest request) {
        User user = loginDAO.getUserByUsername(loginDTO.getUsername());

        if (user != null && BCrypt.checkpw(loginDTO.getPassword(), user.getPassword())) {
            AuditLogDTO log = new AuditLogDTO(
                    user.getUserId(),
                    "LOGIN",
                    "USER",
                    null,
                    "User logged in",
                    request.getRemoteAddr()
            );
            auditLogDAO.saveAuditLog(log);
            return user;
        }

        return null;
    }
}
