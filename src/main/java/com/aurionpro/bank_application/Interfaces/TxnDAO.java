package com.aurionpro.bank_application.Interfaces;

import com.aurionpro.bank_application.DAO.TransactionDTO;
import com.aurionpro.bank_application.Models.Transaction;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

public interface TxnDAO{

    // 1. Create Transaction
    boolean createTransaction(Transaction txn) throws SQLException;

    // 2. Get transaction by ID
    Transaction getTransactionById(int txnId) throws SQLException;

    // 3. Get all transactions for account
    List<Transaction> getTransactionsByAccountNumber(int accountNumber) throws SQLException;

    // 4. Recent N transactions
    List<Transaction> getRecentTransactions(int accountNumber, int limit) throws SQLException;

    // 5. Get transactions in date range
    List<Transaction> getTransactionsByDateRange(int accountNumber, LocalDateTime from, LocalDateTime to) throws SQLException;

    // 6. Total Sent
    long getTotalSent(int senderAccountNumber) throws SQLException;

    // 7. Total Received
    long getTotalReceived(int receiverAccountNumber) throws SQLException;

    // 8. Delete transaction
    boolean deleteTransaction(int txnId) throws SQLException;

    List<TransactionDTO> findTransactionsByAccountNumber(long accountNumber);
    List<TransactionDTO> findFilteredTransactions(long accountNumber, Map<String, String> filters);
}
