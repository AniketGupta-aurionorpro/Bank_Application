package com.aurionpro.bank_application.Services;

import com.aurionpro.bank_application.DAO.LoginDAOImpl;
import com.aurionpro.bank_application.ENUMS.Roles;
import com.aurionpro.bank_application.Interfaces.LoginDAO;
import com.aurionpro.bank_application.Models.User;
import com.aurionpro.bank_application.Util.EncryptionUtil;
import jakarta.annotation.Resource;

import javax.sql.DataSource;

public class LoginService {
    String username;
    String password;
    EncryptionUtil encryptionUtil;
    LoginDAO loginDAO;
    User user;

    public LoginService(DataSource dataSource) {
        encryptionUtil = new EncryptionUtil();
        user = new User();
        loginDAO = new LoginDAOImpl(dataSource);
    }
    public User login(String username, String password) {
        this.username = username;
        this.password = password;
        user = loginDAO.getUserByUsername(username);
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
