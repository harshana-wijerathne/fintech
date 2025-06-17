package site.wijerathne.harshana.fintech.service;

import org.apache.commons.dbcp2.BasicDataSource;
import org.mindrot.jbcrypt.BCrypt;
import site.wijerathne.harshana.fintech.dao.LoginDAO;
import site.wijerathne.harshana.fintech.model.User;

public class LoginService {
    public boolean validateUser(String username, String password) {
        User user = new LoginDAO().getUser(username, password);
        return user != null && BCrypt.checkpw(password, user.getPassword());
    }
}
