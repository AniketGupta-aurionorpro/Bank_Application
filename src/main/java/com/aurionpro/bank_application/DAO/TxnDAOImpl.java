package com.aurionpro.bank_application.DAO;

import com.aurionpro.bank_application.Interfaces.TxnDAO;
import com.aurionpro.bank_application.Models.Transaction;
import com.aurionpro.bank_application.ENUMS.TXN_TYPE;

import javax.sql.DataSource;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class TxnDAOImpl implements TxnDAO {

    private final DataSource dataSource;

    public TxnDAOImpl(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    // 1. Create Transaction
    @Override
    public boolean createTransaction(Transaction txn) throws SQLException {
        String sql = "INSERT INTO transactions (sender_account_number, receiver_account_number, type, amount, details) VALUES (?, ?, ?, ?, ?)";
        try (
                Connection conn = dataSource.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)
        ) {
            ps.setInt(1, txn.getSenderAccountNumber());

            if (txn.getReceiverAccountNumber() != null) {
                ps.setInt(2, txn.getReceiverAccountNumber());
            } else {
                ps.setNull(2, Types.INTEGER);
            }

            ps.setString(3, txn.getType().name().toLowerCase());
            ps.setLong(4, txn.getAmount());
            ps.setString(5, txn.getDetails());

            return ps.executeUpdate() > 0;
        }
    }

    // 2. Get transaction by ID
    @Override
    public Transaction getTransactionById(int txnId) throws SQLException {
        String sql = "SELECT * FROM transactions WHERE txn_id = ?";
        try (
                Connection conn = dataSource.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)
        ) {
            ps.setInt(1, txnId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToTransaction(rs);
                }
            }
        }
        return null;
    }

    // 3. Get all transactions for account
    @Override
    public List<Transaction> getTransactionsByAccountNumber(int accountNumber) throws SQLException {
        String sql = "SELECT * FROM transactions WHERE sender_account_number = ? OR receiver_account_number = ? ORDER BY txn_date DESC";
        try (
                Connection conn = dataSource.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)
        ) {
            ps.setInt(1, accountNumber);
            ps.setInt(2, accountNumber);
            try (ResultSet rs = ps.executeQuery()) {
                List<Transaction> transactions = new ArrayList<>();
                while (rs.next()) {
                    transactions.add(mapResultSetToTransaction(rs));
                }
                return transactions;
            }
        }
    }

    // 4. Recent N transactions
    @Override
    public List<Transaction> getRecentTransactions(int accountNumber, int limit) throws SQLException {
        String sql = "SELECT * FROM transactions WHERE sender_account_number = ? OR receiver_account_number = ? ORDER BY txn_date DESC LIMIT ?";
        try (
                Connection conn = dataSource.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)
        ) {
            ps.setInt(1, accountNumber);
            ps.setInt(2, accountNumber);
            ps.setInt(3, limit);
            try (ResultSet rs = ps.executeQuery()) {
                List<Transaction> transactions = new ArrayList<>();
                while (rs.next()) {
                    transactions.add(mapResultSetToTransaction(rs));
                }
                return transactions;
            }
        }
    }

    // 5. Get transactions in date range
    @Override
    public List<Transaction> getTransactionsByDateRange(int accountNumber, LocalDateTime from, LocalDateTime to) throws SQLException {
        String sql = "SELECT * FROM transactions WHERE (sender_account_number = ? OR receiver_account_number = ?) AND txn_date BETWEEN ? AND ? ORDER BY txn_date DESC";
        try (
                Connection conn = dataSource.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)
        ) {
            ps.setInt(1, accountNumber);
            ps.setInt(2, accountNumber);
            ps.setTimestamp(3, Timestamp.valueOf(from));
            ps.setTimestamp(4, Timestamp.valueOf(to));
            try (ResultSet rs = ps.executeQuery()) {
                List<Transaction> transactions = new ArrayList<>();
                while (rs.next()) {
                    transactions.add(mapResultSetToTransaction(rs));
                }
                return transactions;
            }
        }
    }

    // 6. Total Sent
    @Override
    public long getTotalSent(int senderAccountNumber) throws SQLException {
        String sql = "SELECT SUM(amount) FROM transactions WHERE sender_account_number = ?";
        try (
                Connection conn = dataSource.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)
        ) {
            ps.setInt(1, senderAccountNumber);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getLong(1);
                }
            }
        }
        return 0;
    }

    // 7. Total Received
    @Override
    public long getTotalReceived(int receiverAccountNumber) throws SQLException {
        String sql = "SELECT SUM(amount) FROM transactions WHERE receiver_account_number = ?";
        try (
                Connection conn = dataSource.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)
        ) {
            ps.setInt(1, receiverAccountNumber);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getLong(1);
                }
            }
        }
        return 0;
    }

    // 8. Delete transaction
    @Override
    public boolean deleteTransaction(int txnId) throws SQLException {
        String sql = "DELETE FROM transactions WHERE txn_id = ?";
        try (
                Connection conn = dataSource.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)
        ) {
            ps.setInt(1, txnId);
            return ps.executeUpdate() > 0;
        }
    }

        private Transaction mapResultSetToTransaction(ResultSet rs) throws SQLException {
        int txnId = rs.getInt("txn_id");
        int sender = rs.getInt("sender_account_number");
        int receiver = rs.getInt("receiver_account_number");
        Integer receiverAccount = rs.wasNull() ? null : receiver;

        TXN_TYPE txnType = TXN_TYPE.valueOf(rs.getString("type").toUpperCase());
        long amount = rs.getLong("amount");
        String details = rs.getString("details");
        LocalDateTime txnDate = rs.getTimestamp("txn_date").toLocalDateTime();

        return new Transaction(txnId, sender, receiverAccount, txnType, amount, details, txnDate);
    }
}
