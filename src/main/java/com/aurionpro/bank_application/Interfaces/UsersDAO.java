package com.aurionpro.bank_application.Interfaces;

import com.aurionpro.bank_application.Models.User;

import java.util.List;

public interface UsersDAO {

    public User getUserByUsername(String username);
    public List<User> getAllUsers();
    public boolean updateUserStatus(int userId, String status);
    public boolean deleteUser(int userId);
    public boolean createUser(User user);
    public boolean updateUser(User user);
    public User getUserByAccountNumber(int accountNumber);
    public boolean isUsernameExists(String username);
    public List<User> getUserByRole(String email);
    boolean isEmailExists(String email);
    boolean isPhoneExists(String phone);
    boolean isAccountNumberExists(int accountNumber);
}
