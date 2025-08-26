package com.aurionpro.bank_application.Services;

import com.aurionpro.bank_application.DAO.UserDAOImpl;
import com.aurionpro.bank_application.Interfaces.UsersDAO;
import com.aurionpro.bank_application.Models.User;
import com.aurionpro.bank_application.Util.EncryptionUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import javax.sql.DataSource;
import java.util.Random;


public class AddCustomerServices {

    private final UsersDAO userDAO;

    public AddCustomerServices(DataSource dataSource) {
        this.userDAO = new UserDAOImpl(dataSource);
    }


    public boolean addCustomer(User newUser) throws Exception {
        EncryptionUtil encryptionUtil = new EncryptionUtil();

        String hashedPassword = encryptionUtil.hashPassword(newUser.getPassword());
        newUser.setPassword(hashedPassword);

       newUser.setAccountNumber(generateUniqueAccountNumber());

       newUser.setRole("customer");
        newUser.setStatus("created");
        newUser.setBalance(0L);
        newUser.setIs_deleted(false);

        boolean success = userDAO.createUser(newUser);

        if (success) {
            return true;
        } else {
            throw new Exception("Failed to create customer in the database.");
        }
    }


    private int generateUniqueAccountNumber() {
        Random random = new Random();
        int accountNumber;
        do {
            accountNumber = 100_000_000 + random.nextInt(900_000_000);
        } while (userDAO.isAccountNumberExists(accountNumber));
        return accountNumber;
    }



    public boolean isUsernameTaken(String username) {
        return userDAO.isUsernameExists(username);
    }

    public boolean isEmailTaken(String email) {
        return userDAO.isEmailExists(email);
    }

    public boolean isPhoneTaken(String phone) {
        return userDAO.isPhoneExists(phone);
    }
}