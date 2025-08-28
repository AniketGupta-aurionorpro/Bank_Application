package com.aurionpro.bank_application.DAO;

import com.aurionpro.bank_application.Models.User;

import java.util.List;

public class CustomerDashboardDTO {

    private User customer;
    private List<TransactionDTO> recentTransactions;

    public CustomerDashboardDTO(User customer, List<TransactionDTO> recentTransactions) {
        this.customer = customer;
        this.recentTransactions = recentTransactions;
    }

    // Getters and Setters
    public User getCustomer() {
        return customer;
    }

    public void setCustomer(User customer) {
        this.customer = customer;
    }

    public List<TransactionDTO> getRecentTransactions() {
        return recentTransactions;
    }

    public void setRecentTransactions(List<TransactionDTO> recentTransactions) {
        this.recentTransactions = recentTransactions;
    }
}