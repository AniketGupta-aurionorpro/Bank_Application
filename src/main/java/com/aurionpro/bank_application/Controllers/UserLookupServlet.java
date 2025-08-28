package com.aurionpro.bank_application.Controllers;

import com.aurionpro.bank_application.DAO.UserDAOImpl;
import com.aurionpro.bank_application.Interfaces.UsersDAO;
import com.aurionpro.bank_application.Models.User;
import com.google.gson.Gson;
import jakarta.annotation.Resource;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import javax.sql.DataSource;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@WebServlet("/lookup-user") // New URL
public class UserLookupServlet extends HttpServlet {
    @Resource(name = "jdbc/bank_app")
    private DataSource dataSource;
    private UsersDAO usersDAO;
    private final Gson gson = new Gson();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        if (dataSource == null) { /* ... same error handling as above ... */ return; }
        if (usersDAO == null) { this.usersDAO = new UserDAOImpl(this.dataSource); }

        Map<String, Object> responseData = new HashMap<>();
        String username = req.getParameter("username"); // Changed from accountNumber

        try {
            User receiver = usersDAO.getUserByUsername(username); // Changed method call

            if (receiver != null) {
                responseData.put("isValid", true);
                responseData.put("receiverName", receiver.getUsername());
                responseData.put("receiverAccount", receiver.getAccountNumber()); // Also return account number
            } else {
                responseData.put("isValid", false);
                responseData.put("message", "User not found.");
            }
        } catch (Exception e) {
            e.printStackTrace();
            responseData.put("isValid", false);
            responseData.put("message", "Server error during lookup.");
        }

        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");
        resp.getWriter().write(gson.toJson(responseData));
    }
}