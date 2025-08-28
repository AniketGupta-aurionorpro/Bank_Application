package com.aurionpro.bank_application.Services;


import com.aurionpro.bank_application.DAO.UserDAOImpl;
import com.aurionpro.bank_application.ENUMS.TXN_TYPE;
import com.aurionpro.bank_application.Interfaces.UsersDAO;
import com.aurionpro.bank_application.Models.User;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class TransactionService {

    private final DataSource dataSource;
    private final UsersDAO usersDAO; // Add UsersDAO
    private final LoginService loginService; // Add LoginService

    public TransactionService(DataSource dataSource) {
        this.dataSource = dataSource;
        this.usersDAO = new UserDAOImpl(dataSource); // Initialize
        this.loginService = new LoginService(dataSource); // Initialize
    }

    // Atomically performs a transfer
    public boolean performTransfer(long senderAccount, long receiverAccount, long amount, String details, String username, String password) throws SQLException, SecurityException {
        // 1. Authorize the transaction by verifying the user's password.
        verifyPassword(username, password);

        // 2. Validate business rules before starting the database transaction.
        if (senderAccount == receiverAccount) {
            throw new SQLException("Cannot transfer funds to the same account.");
        }
        if (amount <= 0) {
            throw new SQLException("Transfer amount must be positive.");
        }

        Connection conn = null;
        try {
            // Get a single connection from the pool for all operations.
            conn = dataSource.getConnection();

            // --- START DATABASE TRANSACTION ---
            // Disable auto-commit to manually control the transaction boundary.
            conn.setAutoCommit(false);

            // 3. Debit the sender's account.
            // The WHERE clause includes a check for sufficient funds (balance >= ?).
            // This is an atomic check-and-update operation.
            String debitSql = "UPDATE users SET balance = balance - ? WHERE account_number = ? AND balance >= ?";
            try (PreparedStatement ps = conn.prepareStatement(debitSql)) {
                ps.setLong(1, amount);
                ps.setLong(2, senderAccount);
                ps.setLong(3, amount);
                int rowsAffected = ps.executeUpdate();
                // If no rows are affected, it means the sender's account was not found OR they had insufficient funds.
                if (rowsAffected == 0) {
                    throw new SQLException("Insufficient funds or sender account not found.");
                }
            }

            // 4. Credit the receiver's account.
            String creditSql = "UPDATE users SET balance = balance + ? WHERE account_number = ?";
            try (PreparedStatement ps = conn.prepareStatement(creditSql)) {
                ps.setLong(1, amount);
                ps.setLong(2, receiverAccount);
                int rowsAffected = ps.executeUpdate();
                // If no rows are affected, the receiver's account does not exist.
                if (rowsAffected == 0) {
                    // Because the sender has already been debited, we MUST throw an exception
                    // to trigger the rollback.
                    throw new SQLException("Receiver account not found.");
                }
            }

            // 5. Log the successful transaction in the transactions table.
            String logSql = "INSERT INTO transactions (sender_account_number, receiver_account_number, type, amount, details) VALUES (?, ?, ?, ?, ?)";
            try (PreparedStatement ps = conn.prepareStatement(logSql)) {
                ps.setLong(1, senderAccount);
                ps.setLong(2, receiverAccount);
                ps.setString(3, TXN_TYPE.TRANSFER.name().toLowerCase()); // Use ENUM name
                ps.setLong(4, amount);
                ps.setString(5, details);
                ps.executeUpdate();
            }

            // --- COMMIT TRANSACTION ---
            // If all previous steps have succeeded without throwing an exception, commit the changes to the database.
            conn.commit();

            // The operation was successful.
            return true;

        } catch (SQLException | SecurityException e) {
            // --- ROLLBACK TRANSACTION ---
            // If any step in the 'try' block fails, this 'catch' block is executed.
            if (conn != null) {
                try {
                    // Revert all changes made on this connection since the last commit.
                    conn.rollback();
                    System.err.println("Transaction rolled back due to error: " + e.getMessage());
                } catch (SQLException ex) {
                    // Log the rollback failure, but the original exception is more important.
                    System.err.println("Error during transaction rollback: " + ex.getMessage());
                }
            }
            // Re-throw the original exception to be handled by the controller layer.
            throw e;
        } finally {
            // --- CLEANUP ---
            // This block always executes, whether the transaction succeeded or failed.
            if (conn != null) {
                try {
                    // Restore the default connection behavior.
                    conn.setAutoCommit(true);
                    // Return the connection to the pool.
                    conn.close();
                } catch (SQLException e) {
                    e.printStackTrace(); // Log any error during connection closing.
                }
            }
        }
    }

    private void verifyPassword(String username, String password) throws SecurityException {
        User user = loginService.login(username, password);
        if (user == null) {
            throw new SecurityException("Invalid password. Transaction denied.");
        }
    }

    // Atomically performs a deposit (credit)
    public boolean performCredit(long accountNumber, long amount, String details, String username, String password) throws SQLException, SecurityException {
        verifyPassword(username, password);
        Connection conn = null;
        try {
            conn = dataSource.getConnection();
            conn.setAutoCommit(false);

            // 1. Update user's balance
            String updateSql = "UPDATE users SET balance = balance + ? WHERE account_number = ?";
            try (PreparedStatement ps = conn.prepareStatement(updateSql)) {
                ps.setLong(1, amount);
                ps.setLong(2, accountNumber);
                ps.executeUpdate();
            }

            // 2. Log the transaction
            String logSql = "INSERT INTO transactions (sender_account_number, type, amount, details) VALUES (?, ?, ?, ?)";
            try (PreparedStatement ps = conn.prepareStatement(logSql)) {
                ps.setLong(1, accountNumber);
                ps.setString(2, TXN_TYPE.CREDIT.name());
                ps.setLong(3, amount);
                ps.setString(4, details);
                ps.executeUpdate();
            }

            conn.commit();
            return true;
        } catch (SQLException e) {
            if (conn != null) conn.rollback();
            throw e; // Re-throw to be handled by the controller
        } finally {
            if (conn != null) {
                conn.setAutoCommit(true);
                conn.close();
            }
        }
    }

    // Atomically performs a withdrawal (debit)
    public boolean performDebit(long accountNumber, long amount, String details, String username, String password) throws SQLException, SecurityException {
        verifyPassword(username, password);
        Connection conn = null;
        try {
            conn = dataSource.getConnection();
            conn.setAutoCommit(false);

            // 1. Update user's balance with a check for sufficient funds
            String updateSql = "UPDATE users SET balance = balance - ? WHERE account_number = ? AND balance >= ?";
            try (PreparedStatement ps = conn.prepareStatement(updateSql)) {
                ps.setLong(1, amount);
                ps.setLong(2, accountNumber);
                ps.setLong(3, amount);
                int rowsAffected = ps.executeUpdate();
                if (rowsAffected == 0) {
                    throw new SQLException("Insufficient funds.");
                }
            }

            // 2. Log the transaction
            String logSql = "INSERT INTO transactions (sender_account_number, type, amount, details) VALUES (?, ?, ?, ?)";
            try (PreparedStatement ps = conn.prepareStatement(logSql)) {
                ps.setLong(1, accountNumber);
                ps.setString(2, TXN_TYPE.DEBIT.name());
                ps.setLong(3, amount);
                ps.setString(4, details);
                ps.executeUpdate();
            }

            conn.commit();
            return true;
        } catch (SQLException e) {
            if (conn != null) conn.rollback();
            throw e;
        } finally {
            if (conn != null) {
                conn.setAutoCommit(true);
                conn.close();
            }
        }
    }
}