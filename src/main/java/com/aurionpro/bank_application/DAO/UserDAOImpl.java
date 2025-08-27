package com.aurionpro.bank_application.DAO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource;

import com.aurionpro.bank_application.Exceptions.AuthenticationException;
import com.aurionpro.bank_application.Interfaces.UsersDAO;
import com.aurionpro.bank_application.Models.User;

public class UserDAOImpl implements UsersDAO {


    private final DataSource dataSource;
    public UserDAOImpl(DataSource dataSource) {
        this.dataSource = dataSource;
    }
    public User getUserByUsername(String username) {
        User user = null;
        String query = "SELECT * FROM users WHERE username = ? AND is_deleted = FALSE";

        try (Connection connection = dataSource.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setString(1, username);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    user = mapRowToUser(resultSet);
                }
            }
        } catch (SQLException e) {
            throw new AuthenticationException("Not able to fetch data"); // Or use a logger
        }
        return user;
    }

    @Override
    public List<User> getAllUsers() {
        List<User> users = new ArrayList<>();
        String query = "SELECT * FROM users";

        try (Connection connection = dataSource.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query);
             ResultSet resultSet = preparedStatement.executeQuery()) {

            while (resultSet.next()) {
                users.add(mapRowToUser(resultSet));
            }
        } catch (SQLException e) {
            throw new AuthenticationException("Not able to fetch data"); // Or use a logger
        }
        return users;
    }

    @Override
    public boolean updateUserStatus(int userId, String status) {
        String sql = "UPDATE users SET status = ? WHERE user_id = ?";
        try (Connection connection = dataSource.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setString(1, status);
            preparedStatement.setInt(2, userId);
            int rowsAffected = preparedStatement.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            throw new AuthenticationException("Not able to update user status"); // Or use a logger
        }
    }

    @Override
    public boolean deleteUser(int userId) {
        String sql = "UPDATE users SET is_deleted = TRUE WHERE user_id = ?";
        try (Connection connection = dataSource.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setInt(1, userId);
            int rowsAffected = preparedStatement.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            throw new AuthenticationException("Not able to delete user"); // Or use a logger
        }
    }

    @Override
    public boolean createUser(User user) {
        String sql = "INSERT INTO users (account_number, username, password, email, phone, role, balance, created_at, status, is_deleted) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection connection = dataSource.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setInt(1, user.getAccountNumber());
            preparedStatement.setString(2, user.getUsername());
            preparedStatement.setString(3, user.getPassword());
            preparedStatement.setString(4, user.getEmail());
            preparedStatement.setString(5, user.getPhone());
            preparedStatement.setString(6, user.getRole());
            preparedStatement.setLong(7, user.getBalance());
            preparedStatement.setTimestamp(8, Timestamp.valueOf(LocalDateTime.now()));
            preparedStatement.setString(9, user.getStatus());
            preparedStatement.setBoolean(10, user.isIs_deleted());
            int rowsAffected = preparedStatement.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            throw new AuthenticationException("Not able to create user"); // Or use a logger
        }
    }

    @Override
    public boolean updateUser(User user) {
        String sql = "UPDATE users SET account_number = ?, username = ?, password = ?, email = ?, phone = ?, role = ?, balance = ?, status = ?, is_deleted = ? WHERE user_id = ?";
        try (Connection connection = dataSource.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setInt(1, user.getAccountNumber());
            preparedStatement.setString(2, user.getUsername());
            preparedStatement.setString(3, user.getPassword());
            preparedStatement.setString(4, user.getEmail());
            preparedStatement.setString(5, user.getPhone());
            preparedStatement.setString(6, user.getRole());
            preparedStatement.setLong(7, user.getBalance());
            preparedStatement.setString(8, user.getStatus());
            preparedStatement.setBoolean(9, user.isIs_deleted());
            preparedStatement.setInt(10, user.getId());
            int rowsAffected = preparedStatement.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            throw new AuthenticationException("Not able to update user"); // Or use a logger
        }
    }

    @Override
    public User getUserByAccountNumber(int accountNumber) {
        String sql = "SELECT * FROM users WHERE account_number = ?";
        try (Connection connection = dataSource.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setInt(1, accountNumber);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                return mapRowToUser(resultSet);
            }
        } catch (SQLException e) {
            throw new AuthenticationException("Not able to retrieve user"); // Or use a logger
        }
        return null;
    }

    private User mapRowToUser(ResultSet resultSet) {
        try {
            User user = new User();
            user.setId(resultSet.getInt("user_id"));
            user.setAccountNumber(resultSet.getInt("account_number"));
            user.setUsername(resultSet.getString("username"));
            user.setPassword(resultSet.getString("password"));
            user.setEmail(resultSet.getString("email"));
            user.setPhone(resultSet.getString("phone"));
            user.setRole(resultSet.getString("role"));
            user.setBalance(resultSet.getLong("balance"));
            user.setCreated_at(resultSet.getTimestamp("created_at").toLocalDateTime());
            user.setStatus(resultSet.getString("status"));
            user.setIs_deleted(resultSet.getBoolean("is_deleted"));
            return user;
        } catch (SQLException e) {
            throw new AuthenticationException("Error mapping row to User"); // Or use a logger
        }
    }
    @Override
    public boolean isUsernameExists(String username) {
        String sql = "SELECT COUNT(*) FROM users WHERE username = ?";
        try (Connection connection = dataSource.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setString(1, username);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                return resultSet.getInt(1) > 0;
            }
        } catch (SQLException e) {
            throw new AuthenticationException("Not able to check if username exists"); // Or use a logger
        }
       return false;
    }

    @Override
    public List<User> getUserByRole(String role) {
        List<User> users = new ArrayList<>();
        String query = "SELECT * FROM users WHERE role = ? AND is_deleted = FALSE";
        try (Connection connection = dataSource.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setString(1, role);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                while(resultSet.next()) {
                    users.add(mapRowToUser(resultSet));
                }
            }
        } catch (SQLException e) {
            throw new AuthenticationException("Not able to fetch data"); // Or use a logger
        }
        return users;
    }

    @Override
    public List<User> getUserByRole1(String role) {
        List<User> users = new ArrayList<>();
        String query = "SELECT * FROM users WHERE role = ? ";
        try (Connection connection = dataSource.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setString(1, role);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                while(resultSet.next()) {
                    users.add(mapRowToUser(resultSet));
                }
            }
        } catch (SQLException e) {
            throw new AuthenticationException("Not able to fetch data"); // Or use a logger
        }
        return users;
    }

    @Override
    public User getUserById(int id) {
        String sql = "SELECT * FROM users WHERE user_id = ?";
        try (Connection connection = dataSource.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setInt(1, id);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                return mapRowToUser(resultSet);
            }
        } catch (SQLException e) {
            throw new AuthenticationException("Not able to retrieve user"); // Or use a logger
        }
        return null;
    }

    @Override
    public boolean isEmailExists(String email) {
        String sql = "SELECT COUNT(*) FROM users WHERE email = ?";
        try (Connection connection = dataSource.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setString(1, email);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getInt(1) > 0;
                }
            }
        } catch (SQLException e) {
            throw new AuthenticationException("Error checking if email exists");
        }
        return false;
    }

    @Override
    public boolean isPhoneExists(String phone) {
        String sql = "SELECT COUNT(*) FROM users WHERE phone = ?";
        try (Connection connection = dataSource.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setString(1, phone);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getInt(1) > 0;
                }
            }
        } catch (SQLException e) {
            throw new AuthenticationException("Error checking if phone exists");
        }
        return false;
    }

    @Override
    public boolean isAccountNumberExists(int accountNumber) {
        String sql = "SELECT COUNT(*) FROM users WHERE account_number = ?";
        try (Connection connection = dataSource.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setInt(1, accountNumber);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getInt(1) > 0;
                }
            }
        } catch (SQLException e) {
            throw new AuthenticationException("Error checking if account number exists");
        }
        return false;
    }
}
