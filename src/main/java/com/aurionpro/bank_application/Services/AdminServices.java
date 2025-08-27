package com.aurionpro.bank_application.Services;
//
//import com.aurionpro.bank_application.DAO.UserDAOImpl;
//import com.aurionpro.bank_application.Interfaces.UsersDAO;
//import com.aurionpro.bank_application.Models.User;
//
//import javax.sql.DataSource;
//import java.util.List;
//import java.util.stream.Collectors;
//
//public class AdminServices {
//
//    private final UsersDAO userDAO;
//
//    public AdminServices(DataSource dataSource) {
//
//        this.userDAO = new UserDAOImpl(dataSource);
//    }
//
//
//    public List<User> getAllCustomers() {
//        return userDAO.getUserByRole1("customer");
//    }
//
//
//    public boolean softDeleteCustomer(int userId) {
//        return userDAO.deleteUser(userId);
//    }
//
//
//    public List<User> getPendingCustomers() {
//        List<User> allUsers = userDAO.getAllUsers();
//
//        return allUsers.stream()
//                .filter(user -> "pending".equalsIgnoreCase(user.getStatus()))
//                .collect(Collectors.toList());
//    }
//
//
//    public boolean approveCustomer(int userId) {
//        return userDAO.updateUserStatus(userId, "created");
//    }
//
//
//    public boolean rejectCustomer(int userId) {
//        return userDAO.deleteUser(userId);
//    }
//
//
//    public User getCustomerById(int userId) {
//        return userDAO.getAllUsers().stream()
//                .filter(user -> user.getId() == userId)
//                .findFirst()
//                .orElse(null);
//    }
//
//
//    public boolean updateCustomer(User user) {
//         if ("admin".equalsIgnoreCase(user.getRole())) {
//             throw new SecurityException("Cannot promote user to admin via this method.");
//         }
//        return userDAO.updateUser(user);
//    }
//
//
//    public boolean addCustomer(User newUser) {
//
//        if (userDAO.isUsernameExists(newUser.getUsername())) {
//
//            return false;
//        }
//        newUser.setRole("customer");
//        newUser.setStatus("created");
//        newUser.setIs_deleted(false);
//
//        return userDAO.createUser(newUser);
//    }
//
//    // public List<Transaction> getAllTransactions() {
//    //     return transactionDAO.getAllTransactions();
//    // }
//}


import com.aurionpro.bank_application.DAO.TransactionDTO;
import com.aurionpro.bank_application.DAO.TxnDAOImpl;
import com.aurionpro.bank_application.DAO.UserDAOImpl;
import com.aurionpro.bank_application.Interfaces.TxnDAO;
import com.aurionpro.bank_application.Interfaces.UsersDAO;
import com.aurionpro.bank_application.Models.User;

import javax.sql.DataSource;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class AdminServices {

    private final UsersDAO userDAO;
    private final TxnDAO txnDAO;

    public AdminServices(DataSource dataSource) {
        this.userDAO = new UserDAOImpl(dataSource);
        this.txnDAO = new TxnDAOImpl(dataSource); // Initialize your TxnDAO implementation here if needed
    }

    public List<User> getAllCustomers() {
        return userDAO.getUserByRole1("customer");
    }


    public List<User> searchCustomers(String query) {
        List<User> allCustomers = getAllCustomers();
        if (query == null || query.trim().isEmpty()) {
            return allCustomers; // Return all if query is empty
        }

        String lowerCaseQuery = query.toLowerCase();

        return allCustomers.stream()
                .filter(customer ->
                        (customer.getUsername() != null && customer.getUsername().toLowerCase().contains(lowerCaseQuery)) ||
                                (customer.getAccountNumber() != null && String.valueOf(customer.getAccountNumber()).contains(lowerCaseQuery))
                )
                .collect(Collectors.toList());
    }

    // NEW: Get total number of customers
    public int getTotalCustomersCount() {
        return getAllCustomers().size();
    }

    // NEW: Get total balance of all active customers
    public long getTotalBalance() {
        return getAllCustomers().stream()
                .filter(customer -> !customer.isIs_deleted()) // Only sum active customer balances
                .mapToLong(User::getBalance)
                .sum();
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
        return userDAO.getUserById(userId);
    }

    public boolean updateCustomer(User user) {
        if ("admin".equalsIgnoreCase(user.getRole())) {
            throw new SecurityException("Cannot promote user to admin via this method.");
        }
        return userDAO.updateUser(user);
    }

    public boolean addCustomer(User newUser) {
        if (userDAO.isUsernameExists(newUser.getUsername())) {
            return false;
        }
        newUser.setRole("customer");
        newUser.setStatus("created");
        newUser.setIs_deleted(false);
        // NEW: Assign a unique account number if not already handled by DAO
        if (newUser.getAccountNumber() == null || newUser.getAccountNumber() == 0) {
            newUser.setAccountNumber(Math.toIntExact(generateUniqueAccountNumber()));
        }
        return userDAO.createUser(newUser);
    }

    // Simple account number generation, ideally should be more robust/transactional
    private Long generateUniqueAccountNumber() {
        long accountNumber;
        do {
            accountNumber = 1000000000L + (long)(Math.random() * 9000000000L); // 10-digit number
        } while (userDAO.isAccountNumberExists((int) accountNumber)); // Assuming isAccountNumberExists in DAO
        return accountNumber;
    }

    public List<TransactionDTO> getCustomerTransactions(long accountNumber, Map<String, String> filters) {
        return txnDAO.findFilteredTransactions(accountNumber, filters);
    }

}