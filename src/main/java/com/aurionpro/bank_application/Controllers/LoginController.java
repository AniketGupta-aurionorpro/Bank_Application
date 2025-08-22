package com.aurionpro.bank_application.Controllers;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

@WebServlet("/login")
public class LoginController  extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.getRequestDispatcher("/Views/Login.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String username = req.getParameter("username");
        String password = req.getParameter("password");

        // Here you would typically validate the username and password against a database or service
        if ("admin".equals(username) && "password".equals(password)) {
            req.getSession().setAttribute("user", username);
            resp.sendRedirect(req.getContextPath() + "/dashboard");
        } else {
            req.setAttribute("error", "Invalid username or password");
            req.getRequestDispatcher("/Views/Login.jsp").forward(req, resp);
        }
    }
}
