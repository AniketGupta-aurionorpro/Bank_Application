package com.aurionpro.bank_application.Services;

import com.aurionpro.bank_application.DAO.UserDAOImpl;
import com.aurionpro.bank_application.Interfaces.UsersDAO;
import com.aurionpro.bank_application.Models.User;
import com.aurionpro.bank_application.Util.EncryptionUtil;

import javax.sql.DataSource;

public class LoginService {
    String username;
    String password;
    EncryptionUtil encryptionUtil;
    UsersDAO usersDAO;
    User user;

    public LoginService(DataSource dataSource) {
        encryptionUtil = new EncryptionUtil();
        user = new User();
        usersDAO = new UserDAOImpl(dataSource);
    }
    public User login(String username, String password) {
        this.username = username;
        this.password = password;
        user = usersDAO.getUserByUsername(username);
        if (user != null) {
            if (encryptionUtil.checkPassword(password, user.getPassword())) {
                return user;
            } else {
                System.out.println("Invalid Password");
                return null;
            }
        } else {
            System.out.println("User Not Found");
            return null;
        }
    }

}
