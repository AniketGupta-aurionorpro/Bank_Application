package com.aurionpro.bank_application.DAO;

import jakarta.annotation.Resource;
import jakarta.servlet.annotation.WebServlet;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

@WebServlet("/db")
public class LoginDAO {
    @Resource(name = "jdbc/mydb")
    private DataSource dataSource;


}
