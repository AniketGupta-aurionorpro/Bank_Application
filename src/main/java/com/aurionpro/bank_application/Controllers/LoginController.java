package com.aurionpro.bank_application.Controllers;

import com.aurionpro.bank_application.ENUMS.Roles;
import com.aurionpro.bank_application.Models.User;
import com.aurionpro.bank_application.Services.LoginService;
import jakarta.annotation.Resource;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import javax.sql.DataSource;
import java.io.IOException;
import java.util.Map;

@WebServlet("/login")
public class LoginController extends HttpServlet {

    LoginService loginService;
    User user;
    @Resource(name = "jdbc/bank_app")
    private DataSource dataSource;


    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.getRequestDispatcher("/Views/Login.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String username = req.getParameter("username");
        String password = req.getParameter("password");
        HttpSession session = req.getSession();
        this.loginService = new LoginService(dataSource);
        user = loginService.login(username,password);
        if(user!=null){
            if(user.getRole().equals(Roles.ADMIN.toString().toLowerCase())){
                session.setAttribute("username", user.getUsername());
                session.setAttribute("role", user.getRole().toLowerCase());
                session.setMaxInactiveInterval(900);

                System.out.println("Admin Logged In Successfully");
            }
            else if(user.getRole().equals(Roles.CUSTOMER.toString())){
                session.setAttribute("username", user.getUsername());
                session.setAttribute("role", user.getRole().toLowerCase());
                session.setMaxInactiveInterval(1800);
                System.out.println("Customer Logged In Successfully");
            }
        }else {
            req.setAttribute("GLOBAL_ERROR", "Invalid Credentials");
            req.getRequestDispatcher("/Views/Login.jsp").forward(req, resp);
        }
    }
}
