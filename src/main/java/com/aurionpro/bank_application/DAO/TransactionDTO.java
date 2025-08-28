package com.aurionpro.bank_application.DAO;

import com.aurionpro.bank_application.ENUMS.TXN_TYPE;
import com.aurionpro.bank_application.Models.Transaction;
import java.time.format.DateTimeFormatter;

import java.time.LocalDateTime;

public class TransactionDTO extends Transaction {
    private String receiverName;
    private String senderName;
    private static final DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd-MMM-yyyy, hh:mm a");

    // Constructors
    public TransactionDTO() {
        super();
    }


    public TransactionDTO(int txnId, int senderAccountNumber, Integer receiverAccountNumber,
                          TXN_TYPE type, long amount, String details, LocalDateTime txnDate, String receiverName) {
        super(txnId, senderAccountNumber, receiverAccountNumber, type, amount, details, txnDate);
        this.receiverName = receiverName;
    }

    public TransactionDTO(int txnId, int senderAccountNumber, Integer receiverAccountNumber,
                          TXN_TYPE type, long amount, String details, LocalDateTime txnDate,
                          String senderName, String receiverName) { // MODIFIED
        super(txnId, senderAccountNumber, receiverAccountNumber, type, amount, details, txnDate);
        this.senderName = senderName; // NEW
        this.receiverName = receiverName;
    }

    // Getter and Setter
    public String getReceiverName() {
        return receiverName;
    }

    public void setReceiverName(String receiverName) {
        this.receiverName = receiverName;
    }

    public String getFormattedTxnDate() {
        if (this.getTxnDate() != null) {
            return this.getTxnDate().format(dtf);
        }
        return ""; // Or some default text
    }

    public String getSenderName() { return senderName; } // NEW
    public void setSenderName(String senderName) { this.senderName = senderName; }
}
