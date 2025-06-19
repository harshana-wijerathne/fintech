package site.wijerathne.harshana.fintech.service.auth;

import site.wijerathne.harshana.fintech.dto.auth.LoginRequestDTO;
import site.wijerathne.harshana.fintech.model.User;

import javax.servlet.http.HttpServletRequest;
import java.sql.Connection;

public interface LoginService {
    User authenticate(LoginRequestDTO loginDTO, HttpServletRequest request, Connection connection);
}
