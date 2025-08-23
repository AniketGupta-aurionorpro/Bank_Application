package com.aurionpro.bank_application.Models;

import com.aurionpro.bank_application.ENUMS.TXN_TYPE;

import java.time.LocalDateTime;

public class Transaction {

    private int txnId;
    private int senderAccountNumber;
    private Integer receiverAccountNumber; // Nullable
    private TXN_TYPE type; // Store as string using ENUM
    private long amount;
    private String details;
    private LocalDateTime txnDate;

    // No-arg constructor
    public Transaction() {}

    // Constructor without txnId (for insert operations)
    public Transaction(int senderAccountNumber, Integer receiverAccountNumber,
                       TXN_TYPE type, long amount, String details, LocalDateTime txnDate) {
        this.senderAccountNumber = senderAccountNumber;
        this.receiverAccountNumber = receiverAccountNumber;
        this.type = TXN_TYPE.valueOf(type.name());
        this.amount = amount;
        this.details = details;
        this.txnDate = txnDate;
    }

    // Constructor with txnId (for fetching from DB)
    public Transaction(int txnId, int senderAccountNumber, Integer receiverAccountNumber,
                       TXN_TYPE type, long amount, String details, LocalDateTime txnDate) {
        this.txnId = txnId;
        this.senderAccountNumber = senderAccountNumber;
        this.receiverAccountNumber = receiverAccountNumber;
        this.type = TXN_TYPE.valueOf(type.name());
        this.amount = amount;
        this.details = details;
        this.txnDate = txnDate;
    }

    // Getters and Setters
    public int getTxnId() {
        return txnId;
    }

    public void setTxnId(int txnId) {
        this.txnId = txnId;
    }

    public int getSenderAccountNumber() {
        return senderAccountNumber;
    }

    public void setSenderAccountNumber(int senderAccountNumber) {
        this.senderAccountNumber = senderAccountNumber;
    }

    public Integer getReceiverAccountNumber() {
        return receiverAccountNumber;
    }

    public void setReceiverAccountNumber(Integer receiverAccountNumber) {
        this.receiverAccountNumber = receiverAccountNumber;
    }

    public TXN_TYPE getType() {
        return type;
    }

    public void setType(TXN_TYPE type) {
        this.type = type;
    }

    public long getAmount() {
        return amount;
    }

    public void setAmount(long amount) {
        this.amount = amount;
    }

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }

    public LocalDateTime getTxnDate() {
        return txnDate;
    }

    public void setTxnDate(LocalDateTime txnDate) {
        this.txnDate = txnDate;
    }

    @Override
    public String toString() {
        return "Transaction{" +
                "txnId=" + txnId +
                ", senderAccountNumber=" + senderAccountNumber +
                ", receiverAccountNumber=" + receiverAccountNumber +
                ", type='" + type + '\'' +
                ", amount=" + amount +
                ", details='" + details + '\'' +
                ", txnDate=" + txnDate +
                '}';
    }
}
