package com.aurionpro.bank_application.Controllers;

import com.aurionpro.bank_application.DAO.TransactionDTO;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
        if (action == null || action.isEmpty()) {
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
            case "viewTransactions":
                viewCustomerTransactions(req, resp);
                break;
            case "searchCustomers":
                searchCustomers(req, resp);
                break;
            default:
                listCustomers(req, resp);
        }
    }

    private void viewCustomerTransactions(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            int userId = Integer.parseInt(req.getParameter("id"));
            User customer = adminServices.getCustomerById(userId);

            if (customer == null || customer.getAccountNumber() == 0) {
                req.getSession().setAttribute("errorMessage", "Customer or account number not found.");
                resp.sendRedirect(req.getContextPath() + "/admin/dashboard");
                return;
            }


            Map<String, String> filters = new HashMap<>();
            filters.put("startDate", req.getParameter("startDate"));
            filters.put("endDate", req.getParameter("endDate"));
            filters.put("txnType", req.getParameter("txnType"));
            filters.put("receiverAccount", req.getParameter("receiverAccount"));


            List<TransactionDTO> transactionList = adminServices.getCustomerTransactions(customer.getAccountNumber(), filters);

            req.setAttribute("customer", customer);
            req.setAttribute("transactionList", transactionList);

            RequestDispatcher dispatcher = req.getRequestDispatcher("/Views/CustomerTransactions.jsp");
            dispatcher.forward(req, resp);

        } catch (NumberFormatException e) {
            req.getSession().setAttribute("errorMessage", "Invalid customer ID.");
            resp.sendRedirect(req.getContextPath() + "/admin/dashboard");
        }
    }
   private void listCustomers(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        List<User> customerList = adminServices.getAllCustomers();
        req.setAttribute("customerList", customerList);


        req.setAttribute("totalCustomers", adminServices.getTotalCustomersCount());
        req.setAttribute("totalBalance", adminServices.getTotalBalance());

        RequestDispatcher dispatcher = req.getRequestDispatcher("/Views/AdminDashboard.jsp");
        dispatcher.forward(req, resp);
    }

    private void searchCustomers(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String query = req.getParameter("query");
        List<User> customerList = adminServices.searchCustomers(query);
        req.setAttribute("customerList", customerList);
        req.setAttribute("currentQuery", query);


        req.setAttribute("totalCustomers", adminServices.getTotalCustomersCount());
        req.setAttribute("totalBalance", adminServices.getTotalBalance());

        RequestDispatcher dispatcher = req.getRequestDispatcher("/Views/AdminDashboard.jsp");
        dispatcher.forward(req, resp);
    }


    private void deleteCustomer(HttpServletRequest req, HttpServletResponse resp) throws IOException {
    int userId = Integer.parseInt(req.getParameter("id"));

    adminServices.softDeleteCustomer(userId);

    resp.sendRedirect(req.getContextPath() + "/admin/dashboard");
    }

    private void viewAllTransactions(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        Map<String, String> filters = new HashMap<>();
        filters.put("startDate", req.getParameter("startDate"));
        filters.put("endDate", req.getParameter("endDate"));
        filters.put("txnType", req.getParameter("txnType"));
        filters.put("customerName", req.getParameter("customerName"));


        List<TransactionDTO> transactionList = adminServices.getAllSystemTransactions(filters);

        req.setAttribute("transactionList", transactionList);

        RequestDispatcher dispatcher = req.getRequestDispatcher("/Views/AllTransactions.jsp");
        dispatcher.forward(req, resp);
    }


    private void showEditCustomerForm(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            int userId = Integer.parseInt(req.getParameter("id"));
            User customer = adminServices.getCustomerById(userId);

            if (customer == null) {
                resp.sendRedirect(req.getContextPath() + "/admin/dashboard?message=CustomerNotFound");
                return;
            }

            req.setAttribute("customer", customer);
            RequestDispatcher dispatcher = req.getRequestDispatcher("/Views/EditCustomerForm.jsp");
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
            action = "LIST_CUSTOMERS";
        }

        switch (action) {
            case "update":
                updateCustomer(request, response);
                break;

            default:
                doGet(request, response);
        }
    }

    private void updateCustomer(HttpServletRequest req, HttpServletResponse resp) throws IOException, ServletException {
        try {
            int userId = Integer.parseInt(req.getParameter("id"));
            String username = req.getParameter("username");
            String email = req.getParameter("email");
            String phone = req.getParameter("phone");
            String dob = req.getParameter("dob");
            String status = req.getParameter("status");


            User existingUser = adminServices.getCustomerById(userId);

            if (existingUser == null) {
                resp.sendRedirect(req.getContextPath() + "/admin/dashboard?message=CustomerNotFound");
                return;
            }


            existingUser.setUsername(username);
            existingUser.setEmail(email);
            existingUser.setPhone(phone);

            if (status != null && status.equals("active")){
                existingUser.setIs_deleted(false);
            }else{
                existingUser.setIs_deleted(true);
            }

            boolean success = adminServices.updateCustomer(existingUser);

            if (success) {
                resp.sendRedirect(req.getContextPath() + "/admin/dashboard?message=CustomerUpdated");
            } else {
                req.setAttribute("customer", existingUser);
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