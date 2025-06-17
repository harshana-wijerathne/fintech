package site.wijerathne.harshana.fintech.dao;

import org.apache.commons.dbcp2.BasicDataSource;
import org.mindrot.jbcrypt.BCrypt;
import site.wijerathne.harshana.fintech.model.User;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class LoginDAO {
    User user = new User();
    public User getUser(String username, String password ) {
        try(
                Connection connection = DatabaseConnection.getConnection();
                PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM users WHERE username = ?");
        ){
            preparedStatement.setString(1,username);
            ResultSet resultSet = preparedStatement.executeQuery();
            if(resultSet.next()) {
                user.setUsername(username);
                user.setPassword(resultSet.getString("password"));
                user.setRole(resultSet.getString("role"));
                return user;
            }
            return null;
        }catch(Exception e){
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }
}
