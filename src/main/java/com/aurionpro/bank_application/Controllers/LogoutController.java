package com.aurionpro.bank_application.Controllers;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;

@WebServlet("/logoutServlet")
public class LogoutController extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession(false);
        if (session != null) {
            session.invalidate();
        }
//        req.setAttribute("LOGOUT_SUCCESS", "You have been logged out.");
//        req.getRequestDispatcher("/index.jsp").forward(req, resp);
        resp.sendRedirect(req.getContextPath() + "/");
    }
}
