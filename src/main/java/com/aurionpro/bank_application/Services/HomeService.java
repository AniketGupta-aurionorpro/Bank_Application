package com.aurionpro.bank_application.Services;

import com.aurionpro.bank_application.Interfaces.TxnDAO;
import com.aurionpro.bank_application.DAO.TxnDAOImpl;
import com.aurionpro.bank_application.Interfaces.UsersDAO;
import com.aurionpro.bank_application.DAO.UserDAOImpl;
import com.aurionpro.bank_application.DAO.CustomerDashboardDTO;
import com.aurionpro.bank_application.Models.User;
import com.aurionpro.bank_application.DAO.TransactionDTO;

import javax.sql.DataSource;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class HomeService {

    private final UsersDAO usersDAO;
    private final TxnDAO transactionDAO;

    public HomeService(DataSource dataSource) {
        this.usersDAO = new UserDAOImpl(dataSource);
        this.transactionDAO = new TxnDAOImpl(dataSource);
    }

    public CustomerDashboardDTO getCustomerDashboardData(String username) {
        User customer = usersDAO.getUserByUsername(username);
        if (customer == null) {
            return null;
        }

        List<TransactionDTO> transactions = transactionDAO.findTransactionsByAccountNumber(customer.getAccountNumber());

        return new CustomerDashboardDTO(customer, transactions);
    }

    public List<TransactionDTO> getCustomerTransactions(String username, Map<String, String> filters) {
        User customer = usersDAO.getUserByUsername(username);
        if (customer == null || customer.getAccountNumber() == 0) {
            return new ArrayList<>();
        }

        return transactionDAO.findFilteredTransactions(customer.getAccountNumber(), filters);
    }
}
