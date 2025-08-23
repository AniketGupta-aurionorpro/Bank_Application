package com.aurionpro.bank_application.Services;

import com.aurionpro.bank_application.DAO.UserDAOImpl;
import com.aurionpro.bank_application.Interfaces.UsersDAO;

import javax.sql.DataSource;

public class AdminServices {

    DataSource dataSource;
    UsersDAO usersDAO;
    public AdminServices(DataSource dataSource) {
        this.dataSource = dataSource;
        this.usersDAO = new UserDAOImpl(dataSource);
    }



}
