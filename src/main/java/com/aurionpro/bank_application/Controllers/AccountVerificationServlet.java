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

@WebServlet("/verify-account")
public class AccountVerificationServlet extends HttpServlet {

    @Resource(name = "jdbc/bank_app")
    private DataSource dataSource;

    private UsersDAO usersDAO;
    private final Gson gson = new Gson();

    // REMOVE the init() method entirely. We will initialize lazily.
    // @Override
    // public void init() throws ServletException { ... }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        // --- LAZY INITIALIZATION and VALIDATION ---
        // This is a robust pattern. It checks dependencies on every request.
        if (dataSource == null) {
            System.err.println("FATAL ERROR in AccountVerificationServlet: DataSource is NULL. Injection failed!");
            // Send a clear error back to the browser to help debug.
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            resp.getWriter().write("{\"isValid\":false, \"message\":\"Server configuration error: DataSource not available.\"}");
            return;
        }
        if (usersDAO == null) {
            this.usersDAO = new UserDAOImpl(this.dataSource);
            System.out.println("AccountVerificationServlet: UserDAO has been successfully initialized.");
        }
        // ------------------------------------------

        Map<String, Object> responseData = new HashMap<>();
        String accNumParam = req.getParameter("accountNumber");

        try {
            long accountNumber = Long.parseLong(accNumParam);
            User receiver = usersDAO.getUserByAccountNumber(accountNumber);

            if (receiver != null) {
                responseData.put("isValid", true);
                responseData.put("receiverName", receiver.getUsername());
            } else {
                responseData.put("isValid", false);
                responseData.put("message", "Account not found.");
            }
        } catch (NumberFormatException e) {
            responseData.put("isValid", false);
            responseData.put("message", "Invalid account number format.");
        } catch (Exception e) {
            // Generic catch block to prevent 500 errors and provide a clean response
            System.err.println("Error during account verification: " + e.getMessage());
            e.printStackTrace();
            responseData.put("isValid", false);
            responseData.put("message", "A server error occurred. Please check logs.");
        }

        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");
        resp.getWriter().write(gson.toJson(responseData));
    }
}
