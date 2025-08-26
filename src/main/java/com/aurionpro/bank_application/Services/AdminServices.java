package com.aurionpro.bank_application.Services;

import com.aurionpro.bank_application.DAO.UserDAOImpl;
import com.aurionpro.bank_application.Interfaces.UsersDAO;
import com.aurionpro.bank_application.Models.User;

import javax.sql.DataSource;
import java.util.List;
import java.util.stream.Collectors;

public class AdminServices {

    private final UsersDAO userDAO;

    public AdminServices(DataSource dataSource) {

        this.userDAO = new UserDAOImpl(dataSource);
    }


    public List<User> getAllCustomers() {
        // This is much more efficient than getting all users and filtering in Java.
        return userDAO.getUserByRole("customer");
    }


    public boolean softDeleteCustomer(int userId) {
        return userDAO.deleteUser(userId);
    }


    public List<User> getPendingCustomers() {
        List<User> allUsers = userDAO.getAllUsers();

        return allUsers.stream()
                .filter(user -> "pending".equalsIgnoreCase(user.getStatus()))
                .collect(Collectors.toList());
    }


    public boolean approveCustomer(int userId) {
        return userDAO.updateUserStatus(userId, "created");
    }


    public boolean rejectCustomer(int userId) {
        return userDAO.deleteUser(userId);
    }


    public User getCustomerById(int userId) {
        return userDAO.getAllUsers().stream()
                .filter(user -> user.getId() == userId)
                .findFirst()
                .orElse(null);
    }


    public boolean updateCustomer(User user) {
        // if ("admin".equalsIgnoreCase(user.getRole())) {
        //     throw new SecurityException("Cannot promote user to admin via this method.");
        // }
        return userDAO.updateUser(user);
    }


    public boolean addCustomer(User newUser) {

        if (userDAO.isUsernameExists(newUser.getUsername())) {

            return false;
        }





        newUser.setRole("customer");
        newUser.setStatus("created");
        newUser.setIs_deleted(false);

        return userDAO.createUser(newUser);
    }

    // public List<Transaction> getAllTransactions() {
    //     return transactionDAO.getAllTransactions();
    // }
}