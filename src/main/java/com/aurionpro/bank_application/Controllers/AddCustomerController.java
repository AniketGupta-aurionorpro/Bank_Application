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
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
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
        RequestDispatcher dispatcher = req.getRequestDispatcher("/Views/AddCustomer.jsp");
        dispatcher.forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String username = req.getParameter("username");
        String password = req.getParameter("password");
        String email = req.getParameter("email");
        String phone = req.getParameter("phone");
        String dobString = req.getParameter("dob");
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
        }else if (!email.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$")) {
            errors.put("email", "Invalid email format.");
        }else if (addCustomerServices.isEmailTaken(email)) {
            errors.put("email", "This email address is already in use.");
        }

        if (phone == null || phone.trim().isEmpty()) {
            errors.put("phone", "Phone number is required.");
        }else if (phone.length() < 10 || phone.length() > 15) {
            errors.put("phone", "Invalid phone number length.");
        }else if (!phone.matches("^[0-9]+$")) {
            errors.put("phone", "Phone number must contain only digits.");
        }else if (addCustomerServices.isPhoneTaken(phone)) {
            errors.put("phone", "This phone number is already in use.");
        }

        if (dobString == null || dobString.trim().isEmpty()) {
            errors.put("dob", "Date of Birth is required.");
        } else {
            try {
                LocalDate dob = LocalDate.parse(dobString); // Default format is yyyy-MM-dd
                LocalDate eighteenYearsAgo = LocalDate.now().minusYears(18);

                if (dob.isAfter(eighteenYearsAgo)) {
                    errors.put("dob", "Customer must be at least 18 years old.");
                }
            } catch (DateTimeParseException e) {
                errors.put("dob", "Invalid date format. Please use the date picker.");
            }
        }

        User formData = new User();
        formData.setUsername(username);
        formData.setEmail(email);
        formData.setPhone(phone);
//        formData.put("dob", dobString);

        if (!errors.isEmpty()) {
            req.setAttribute("ERRORS", errors);
            req.setAttribute("FORM_DATA", formData);
            RequestDispatcher dispatcher = req.getRequestDispatcher("/Views/AddCustomer.jsp");
            dispatcher.forward(req, resp);
        } else {
            User newUser = new User();
            newUser.setUsername(username);
            newUser.setPassword(password);
            newUser.setEmail(email);
            newUser.setPhone(phone);

            try {
                boolean status = addCustomerServices.addCustomer(newUser);
                if (status) {
                    req.getSession().setAttribute("SUCCESS_MESSAGE", "Customer added successfully.");
                    resp.sendRedirect(req.getContextPath() + "/adminDashboard");
                    return;
                } else {
                    errors.put("general", "Failed to create customer. Please try again.");
                    req.setAttribute("ERRORS", errors);
                    req.setAttribute("FORM_DATA", formData);
                    RequestDispatcher dispatcher = req.getRequestDispatcher("/Views/AddCustomer.jsp");
                    dispatcher.forward(req, resp);
                    return;
                }

            } catch (Exception e) {
                e.printStackTrace();
                errors.put("general", "A server error occurred. Could not create customer.");
                req.setAttribute("ERRORS", errors);
                req.setAttribute("FORM_DATA", formData);
                RequestDispatcher dispatcher = req.getRequestDispatcher("/Views/AddCustomer.jsp");
                dispatcher.forward(req, resp);
            }
        }
    }
}