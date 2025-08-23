package com.aurionpro.bank_application.Controllers;

import java.io.IOException;

import com.aurionpro.bank_application.Services.AdminServices;
import jakarta.annotation.Resource;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import javax.sql.DataSource;

@WebServlet(urlPatterns = {"/admin/dashboard", "/admin"})
public class AdminController extends HttpServlet {

    AdminServices adminServices;
    @Resource(name = "jdbc/bank_app")
    private DataSource dataSource;

    @Override
    public void init() throws ServletException {
        super.init();
        adminServices = new AdminServices(dataSource);
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession(false); // false: don’t create if not exists

        if (session == null || session.getAttribute("username") == null && session.getAttribute("role") != "admin" ) {
            resp.sendRedirect(req.getContextPath() + "/login");
            return;
        }

        String username = (String) session.getAttribute("username");
        String role = (String) session.getAttribute("role");
        System.out.println("Admin Logged In: " + username + " with role: " + role);
        req.getRequestDispatcher("/Views/AdminDashboard.jsp").forward(req, resp);


    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

    }


}


//@WebServlet(urlPatterns = {"/admin/dashboard", "/admin"})
//public class AdminController extends HttpServlet {
//
//
//    @Override
//    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException, IOException {
//        // Security Check: Make sure an admin is logged in
//        HttpSession session = request.getSession(false);
//        if (session == null || session.getAttribute("username") == null) {
//            response.sendRedirect(request.getContextPath() + "/login");
//            return;
//        }
////        User user = (User) session.getAttribute("user");
////        if (!"admin".equalsIgnoreCase(user.getRole())) {
////            response.sendRedirect(request.getContextPath() + "/unauthorizedAccess.jsp"); // Or some error page
////            return;
////        }
//
//        String action = request.getParameter("action");
//        if (action == null) {
//            // Default action is to show the dashboard
//            action = "LIST";
//        }
//
//        switch (action) {
//            case "LIST":
//                listCustomers(request, response);
//                break;
//            case "delete":
//                deleteCustomer(request, response);
//                break;
//            // Add cases for "showEdit", "showAddCustomer", "showPending" here
//            default:
//                listCustomers(request, response);
//        }
//    }
//
//    private void listCustomers(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
//        // Fetch all users with the role 'customer' from the database
//        List<User> customers = usersDAO.getUserByRole("customer");
//
//        // Add the list to the request object
//        request.setAttribute("customerList", customers);
//
//        // Forward the request to the JSP page
//        RequestDispatcher dispatcher = request.getRequestDispatcher("/Views/AdminDashboard.jsp");
//        dispatcher.forward(request, response);
//    }
//
//    private void deleteCustomer(HttpServletRequest request, HttpServletResponse response) throws IOException {
//        // Get user ID from the request parameter
//        int userId = Integer.parseInt(request.getParameter("id"));
//
//        // Call DAO method to perform a soft delete (UPDATE users SET is_deleted = true WHERE user_id = ?)
//        usersDAO.deleteUser(userId);
//
//        // Redirect back to the dashboard to see the updated list
//        response.sendRedirect(request.getContextPath() + "/admin/dashboard");
//    }
//}
