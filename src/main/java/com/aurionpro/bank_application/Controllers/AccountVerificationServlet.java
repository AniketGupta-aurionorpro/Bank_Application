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
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

@WebServlet("/verify-account")
public class AccountVerificationServlet extends HttpServlet {

    @Resource(name = "jdbc/bank_app")
    private DataSource dataSource;

    private UsersDAO usersDAO;
    private final Gson gson = new Gson();

    @Override
    public void init() throws ServletException {
        if (dataSource == null) {
            throw new ServletException("FATAL: DataSource is not injected for AccountVerificationServlet!");
        }
        this.usersDAO = new UserDAOImpl(this.dataSource);
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");
        PrintWriter out = resp.getWriter();
        Map<String, Object> responseData = new HashMap<>();

        String accNumParam = req.getParameter("accountNumber");

        if (accNumParam == null || accNumParam.trim().isEmpty()) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            responseData.put("isValid", false);
            responseData.put("message", "Account number parameter is missing.");
            out.write(gson.toJson(responseData));
            return;
        }

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

            resp.setStatus(HttpServletResponse.SC_OK);
            out.write(gson.toJson(responseData));

        } catch (NumberFormatException e) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            responseData.put("isValid", false);
            responseData.put("message", "Invalid account number format.");
            out.write(gson.toJson(responseData));
        } catch (Exception e) {
            System.err.println("!!! SEVERE ERROR in AccountVerificationServlet !!!");
            e.printStackTrace();

            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            responseData.put("isValid", false);
            responseData.put("message", "A server error occurred during verification.");
            out.write(gson.toJson(responseData));
        }
    }
}
