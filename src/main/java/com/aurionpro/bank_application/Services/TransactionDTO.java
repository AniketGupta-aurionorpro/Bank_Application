package com.aurionpro.bank_application.Services;


import com.aurionpro.bank_application.DAO.UserDAOImpl;
import com.aurionpro.bank_application.ENUMS.TXN_TYPE;
import com.aurionpro.bank_application.Interfaces.UsersDAO;
import com.aurionpro.bank_application.Models.User;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class TransactionDTO {

    private final DataSource dataSource;
    private final UsersDAO usersDAO;
    private final LoginService loginService;

    public TransactionDTO(DataSource dataSource) {
        this.dataSource = dataSource;
        this.usersDAO = new UserDAOImpl(dataSource);
        this.loginService = new LoginService(dataSource);
    }


    public boolean performTransfer(long senderAccount, long receiverAccount, long amount, String details, String username, String password) throws SQLException, SecurityException {

        verifyPassword(username, password);


        if (senderAccount == receiverAccount) {
            throw new SQLException("Cannot transfer funds to the same account.");
        }
        if (amount <= 0) {
            throw new SQLException("Transfer amount must be positive.");
        }

        Connection conn = null;
        try {

            conn = dataSource.getConnection();


            conn.setAutoCommit(false);


            String debitSql = "UPDATE users SET balance = balance - ? WHERE account_number = ? AND balance >= ?";
            try (PreparedStatement ps = conn.prepareStatement(debitSql)) {
                ps.setLong(1, amount);
                ps.setLong(2, senderAccount);
                ps.setLong(3, amount);
                int rowsAffected = ps.executeUpdate();
                if (rowsAffected == 0) {
                    throw new SQLException("Insufficient funds or sender account not found.");
                }
            }


            String creditSql = "UPDATE users SET balance = balance + ? WHERE account_number = ?";
            try (PreparedStatement ps = conn.prepareStatement(creditSql)) {
                ps.setLong(1, amount);
                ps.setLong(2, receiverAccount);
                int rowsAffected = ps.executeUpdate();

                if (rowsAffected == 0) {

                    throw new SQLException("Receiver account not found.");
                }
            }


            String logSql = "INSERT INTO transactions (sender_account_number, receiver_account_number, type, amount, details) VALUES (?, ?, ?, ?, ?)";
            try (PreparedStatement ps = conn.prepareStatement(logSql)) {
                ps.setLong(1, senderAccount);
                ps.setLong(2, receiverAccount);
                ps.setString(3, TXN_TYPE.TRANSFER.name().toLowerCase());
                ps.setLong(4, amount);
                ps.setString(5, details);
                ps.executeUpdate();
            }

            conn.commit();


            return true;

        } catch (SQLException | SecurityException e) {

            if (conn != null) {
                try {

                    conn.rollback();
                    System.err.println("Transaction rolled back due to error: " + e.getMessage());
                } catch (SQLException ex) {

                    System.err.println("Error during transaction rollback: " + ex.getMessage());
                }
            }

            throw e;
        } finally {

            if (conn != null) {
                try {

                    conn.setAutoCommit(true);

                    conn.close();
                } catch (SQLException e) {
                    e.printStackTrace();
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


    public boolean performCredit(long accountNumber, long amount, String details, String username, String password) throws SQLException, SecurityException {
        verifyPassword(username, password);
        Connection conn = null;
        try {
            conn = dataSource.getConnection();
            conn.setAutoCommit(false);


            String updateSql = "UPDATE users SET balance = balance + ? WHERE account_number = ?";
            try (PreparedStatement ps = conn.prepareStatement(updateSql)) {
                ps.setLong(1, amount);
                ps.setLong(2, accountNumber);
                ps.executeUpdate();
            }


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
            throw e;
        } finally {
            if (conn != null) {
                conn.setAutoCommit(true);
                conn.close();
            }
        }
    }


    public boolean performDebit(long accountNumber, long amount, String details, String username, String password) throws SQLException, SecurityException {
        verifyPassword(username, password);
        Connection conn = null;
        try {
            conn = dataSource.getConnection();
            conn.setAutoCommit(false);


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