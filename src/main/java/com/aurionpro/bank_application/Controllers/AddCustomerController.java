package com.aurionpro.bank_application.Controllers;

import com.aurionpro.bank_application.Models.User;
// Import the new service
import com.aurionpro.bank_application.Services.AddCustomerServices;
import jakarta.annotation.Resource;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import javax.sql.DataSource;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@WebServlet("/addCustomer")
public class AddCustomerController extends HttpServlet {


    private AddCustomerServices addCustomerServices;

    @Resource(name = "jdbc/bank_app")
    private DataSource dataSource;

    @Override
    public void init() throws ServletException {
        addCustomerServices = new AddCustomerServices(dataSource);
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        RequestDispatcher dispatcher = req.getRequestDispatcher("/Views/add-customer.jsp");
        dispatcher.forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String username = req.getParameter("username");
        String password = req.getParameter("password");
        String email = req.getParameter("email");
        String phone = req.getParameter("phone");

        Map<String, String> errors = new HashMap<>();


        if (username == null || username.trim().isEmpty()) {
            errors.put("username", "Username is required.");
        } else if (addCustomerServices.isUsernameTaken(username)) {
            errors.put("username", "This username is already taken.");
        }

        if (password == null || password.trim().isEmpty()) {
            errors.put("password", "Password is required.");
        } else if (password.length() < 8) {
            errors.put("password", "Password must be at least 8 characters long.");
        }

        if (email == null || email.trim().isEmpty()) {
            errors.put("email", "Email is required.");
        } else if (addCustomerServices.isEmailTaken(email)) {
            errors.put("email", "This email address is already in use.");
        }

        if (phone == null || phone.trim().isEmpty()) {
            errors.put("phone", "Phone number is required.");
        } else if (addCustomerServices.isPhoneTaken(phone)) {
            errors.put("phone", "This phone number is already in use.");
        }

        User formData = new User();
        formData.setUsername(username);
        formData.setEmail(email);
        formData.setPhone(phone);

        if (!errors.isEmpty()) {
            req.setAttribute("ERRORS", errors);
            req.setAttribute("FORM_DATA", formData);
            RequestDispatcher dispatcher = req.getRequestDispatcher("/Views/add-customer.jsp");
            dispatcher.forward(req, resp);
        } else {
            User newUser = new User();
            newUser.setUsername(username);
            newUser.setPassword(password);
            newUser.setEmail(email);
            newUser.setPhone(phone);

            try {
                addCustomerServices.addCustomer(newUser);
                resp.sendRedirect(req.getContextPath() + "/admin/dashboard");
            } catch (Exception e) {
                e.printStackTrace();
                errors.put("general", "A server error occurred. Could not create customer.");
                req.setAttribute("ERRORS", errors);
                req.setAttribute("FORM_DATA", formData);
                RequestDispatcher dispatcher = req.getRequestDispatcher("/Views/add-customer.jsp");
                dispatcher.forward(req, resp);
            }
        }
    }
}