package com.aurionpro.bank_application.DAO;

import com.aurionpro.bank_application.Exceptions.AuthenticationException;
import com.aurionpro.bank_application.Interfaces.LoginDAO;
import com.aurionpro.bank_application.Models.User;
import jakarta.annotation.Resource;

import javax.sql.DataSource;
import java.sql.*;

public class LoginDAOImpl implements LoginDAO {


    private final DataSource dataSource;
    public LoginDAOImpl(DataSource dataSource) {
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
                    user = new User();
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
                }
            }
        } catch (SQLException e) {
            throw new AuthenticationException("Not able to fetch data"); // Or use a logger
        }
        return user;
    }
}
