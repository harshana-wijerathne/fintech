package site.wijerathne.harshana.fintech.service.auth;

import com.zaxxer.hikari.HikariDataSource;
import org.mindrot.jbcrypt.BCrypt;
import site.wijerathne.harshana.fintech.repo.AuditLogRepo;
import site.wijerathne.harshana.fintech.repo.auth.LoginRepo;
import site.wijerathne.harshana.fintech.dto.AuditLogDTO;
import site.wijerathne.harshana.fintech.dto.auth.LoginRequestDTO;
import site.wijerathne.harshana.fintech.model.User;

import javax.servlet.http.HttpServletRequest;
import java.sql.Connection;

public class LoginServiceImpl implements LoginService {

    private final LoginRepo loginRepo;
    private final AuditLogRepo auditLogRepo;

    public LoginServiceImpl(LoginRepo loginRepo, AuditLogRepo auditLogRepo) {
        this.loginRepo = loginRepo;
        this.auditLogRepo = auditLogRepo;
    }

    public User authenticate(LoginRequestDTO loginDTO, HttpServletRequest request, Connection connection) {
        User user = loginRepo.getUserByUsername(loginDTO.getUsername());
        if (user != null && BCrypt.checkpw(loginDTO.getPassword(), user.getPassword())) {
            AuditLogDTO log = new AuditLogDTO(
                    user.getUserId(),
                    "LOGIN",
                    "USER",
                    null,
                    "User logged in",
                    request.getRemoteAddr()
            );
            auditLogRepo.saveAuditLog(log);
            return user;
        }

        return null;
    }
}
