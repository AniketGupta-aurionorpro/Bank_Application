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

        resp.getWriter().println("<h1>Page for 'View All Transactions' is under construction.</h1>");
    }


    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doGet(request, response);
    }
}