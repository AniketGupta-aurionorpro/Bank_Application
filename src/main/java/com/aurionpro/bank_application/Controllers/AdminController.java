package com.aurionpro.bank_application.Controllers;

import com.aurionpro.bank_application.Models.User; // Assuming you have a User model
import com.aurionpro.bank_application.Services.AdminServices;
import jakarta.annotation.Resource;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import javax.sql.DataSource;
import java.io.IOException;
import java.util.List;

@WebServlet(urlPatterns = {"/admin/dashboard", "/admin"})
public class AdminController extends HttpServlet {

    private AdminServices adminServices;

    @Resource(name = "jdbc/bank_app")
    private DataSource dataSource;

    @Override
    public void init() throws ServletException {
        super.init();
        adminServices = new AdminServices(dataSource);
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession(false);


        if (session == null || session.getAttribute("username") == null) {
            resp.sendRedirect(req.getContextPath() + "/login");
            return;
        }


        if (!"admin".equalsIgnoreCase((String) session.getAttribute("role"))) {
            resp.sendRedirect(req.getContextPath() + "/login");
            return;
        }


        String action = req.getParameter("action");

        if (action == null) {
            action = "LIST_CUSTOMERS";
        }

        switch (action) {
            case "showEdit":
                showEditCustomerForm(req, resp);
                break;
            case "delete":
                deleteCustomer(req, resp);
                break;
            case "viewAllTransactions":
                viewAllTransactions(req, resp);
                break;
            default:
                listCustomers(req, resp);
        }
    }

//    private void listCustomers(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
//        // Get customer list from the service
//        List<User> customerList = adminServices.getAllCustomers();
//
//        // Add the list to the REQUEST scope, not the session
//        req.setAttribute("customerList", customerList);
//
//        // Forward to the JSP page
//        RequestDispatcher dispatcher = req.getRequestDispatcher("/Views/AdminDashboard.jsp");
//        dispatcher.forward(req, resp);
//    }
    private void listCustomers(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
    List<User> customerList = adminServices.getAllCustomers();
    req.setAttribute("customerList", customerList);
    RequestDispatcher dispatcher = req.getRequestDispatcher("/Views/AdminDashboard.jsp");
    dispatcher.forward(req, resp);
    }
//    private void deleteCustomer(HttpServletRequest req, HttpServletResponse resp) throws IOException {
//        try {
//            // Get the user ID from the request parameter
//            int userId = Integer.parseInt(req.getParameter("id"));
//
//            // Call the service method to perform a soft delete
//            adminServices.softDeleteCustomer(userId);
//
//            // After deleting, redirect back to the main dashboard to show the updated list.
//            // This is the Post-Redirect-Get (PRG) pattern, which prevents re-deleting on refresh.
//            resp.sendRedirect(req.getContextPath() + "/admin/dashboard");
//        } catch (NumberFormatException e) {
//            // Handle cases where the ID is not a valid number
//            e.printStackTrace();
//            resp.sendRedirect(req.getContextPath() + "/admin/dashboard");
//        }
//    }
    private void deleteCustomer(HttpServletRequest req, HttpServletResponse resp) throws IOException {
    int userId = Integer.parseInt(req.getParameter("id"));
    // The controller delegates the deletion logic to the service.
    adminServices.softDeleteCustomer(userId);

    resp.sendRedirect(req.getContextPath() + "/admin/dashboard");
    }

    private void viewAllTransactions(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        // List<Transaction> allTransactions = adminServices.getAllTransactions();

        // req.setAttribute("transactionList", allTransactions);

        // RequestDispatcher dispatcher = req.getRequestDispatcher("/Views/AllTransactions.jsp");
        // dispatcher.forward(req, resp);

//        resp.getWriter().println("<h1>Page for 'View All Transactions' is under construction.</h1>");
    }

    private void showEditCustomerForm(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            int userId = Integer.parseInt(req.getParameter("id"));
            User customer = adminServices.getCustomerById(userId); // You'll need this method in AdminServices

            if (customer == null) {
                resp.sendRedirect(req.getContextPath() + "/admin/dashboard?message=CustomerNotFound");
                return;
            }

            req.setAttribute("customer", customer);
            RequestDispatcher dispatcher = req.getRequestDispatcher("/Views/EditCustomerForm.jsp"); // Create this JSP
            dispatcher.forward(req, resp);

        } catch (NumberFormatException e) {
            e.printStackTrace();
            resp.sendRedirect(req.getContextPath() + "/admin/dashboard?message=InvalidCustomerId");
        }
    }


    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String action = request.getParameter("action");

        if (action == null) {
            action = "LIST_CUSTOMERS"; // Default action
        }

        switch (action) {
            case "update": // New case for updating customer
                updateCustomer(request, response);
                break;
            // Other POST actions if any, otherwise default to doGet for other actions
            default:
                doGet(request, response); // Or handle other POST requests
        }
    }

    private void updateCustomer(HttpServletRequest req, HttpServletResponse resp) throws IOException, ServletException {
        try {
            int userId = Integer.parseInt(req.getParameter("id"));
            String username = req.getParameter("username");
            String email = req.getParameter("email");
            String phone = req.getParameter("phone");
            String dob = req.getParameter("dob"); // Assuming DOB is part of your User model
            String status = req.getParameter("status"); // "active" or "inactive" from toggle

            // Retrieve existing user to preserve other fields
            User existingUser = adminServices.getCustomerById(userId);

            if (existingUser == null) {
                resp.sendRedirect(req.getContextPath() + "/admin/dashboard?message=CustomerNotFound");
                return;
            }

            // Update only the editable fields
            existingUser.setUsername(username);
            existingUser.setEmail(email);
            existingUser.setPhone(phone);
            // existingUser.setDob(LocalDate.parse(dob)); // Uncomment if you add DOB to User model
            if (status != null && status.equals("active")){
                existingUser.setIs_deleted(false);
            }else{
                existingUser.setIs_deleted(true);
            }

            boolean success = adminServices.updateCustomer(existingUser); // New method in AdminServices

            if (success) {
                resp.sendRedirect(req.getContextPath() + "/admin/dashboard?message=CustomerUpdated");
            } else {
                req.setAttribute("customer", existingUser); // Keep data in form for re-display
                req.setAttribute("errorMessage", "Failed to update customer.");
                RequestDispatcher dispatcher = req.getRequestDispatcher("/Views/EditCustomerForm.jsp");
                dispatcher.forward(req, resp);
            }

        } catch (NumberFormatException e) {
            e.printStackTrace();
            resp.sendRedirect(req.getContextPath() + "/admin/dashboard?message=InvalidCustomerId");
        } catch (Exception e) {
            e.printStackTrace();
            req.setAttribute("errorMessage", "An error occurred during update: " + e.getMessage());
            RequestDispatcher dispatcher = req.getRequestDispatcher("/Views/EditCustomerForm.jsp");
            dispatcher.forward(req, resp);
        }
    }

}