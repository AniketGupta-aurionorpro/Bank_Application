//package com.aurionpro.bank_application.Controllers;
//
//import com.aurionpro.bank_application.DAO.TransactionDTO;
//import com.aurionpro.bank_application.DAO.UserDAOImpl;
//import com.aurionpro.bank_application.ENUMS.TXN_TYPE;
//import com.aurionpro.bank_application.Models.User;
//import com.aurionpro.bank_application.Services.HomeService;
//import com.aurionpro.bank_application.Services.TransactionService;
//import com.aurionpro.bank_application.DAO.CustomerDashboardDTO;
//import com.itextpdf.io.source.ByteArrayOutputStream;
//import com.itextpdf.kernel.pdf.PdfDocument;
//import com.itextpdf.kernel.pdf.PdfWriter;
//import com.itextpdf.layout.Document;
//import com.itextpdf.layout.element.Cell;
//import com.itextpdf.layout.element.Paragraph;
//import com.itextpdf.layout.element.Table;
//import com.itextpdf.layout.properties.TextAlignment;
//import com.itextpdf.layout.properties.UnitValue;
//import jakarta.annotation.Resource;
//import jakarta.servlet.RequestDispatcher;
//import jakarta.servlet.ServletException;
//import jakarta.servlet.annotation.WebServlet;
//import jakarta.servlet.http.HttpServlet;
//import jakarta.servlet.http.HttpServletRequest;
//import jakarta.servlet.http.HttpServletResponse;
//import jakarta.servlet.http.HttpSession;
//import javax.sql.DataSource;
//import java.io.IOException;
//import java.sql.SQLException;
//import java.time.format.DateTimeFormatter;
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//
//// This mapping correctly matches the redirect from your LoginController
//@WebServlet("/customer/home")
//public class HomeController extends HttpServlet {
//
//    @Resource(name = "jdbc/bank_app")
//    private DataSource dataSource;
//
//    private HomeService homeService;
//    private TransactionService transactionService;
//
//    @Override
//    public void init() throws ServletException {
//        super.init();
//        homeService = new HomeService(dataSource);
//        transactionService = new TransactionService(dataSource);
//    }
//
//    @Override
//    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
//        HttpSession session = req.getSession(false);
//
//        // Security Check: Verifies the session attributes set by your LoginController.
//        if (session == null || session.getAttribute("username") == null) {
//            resp.sendRedirect(req.getContextPath() + "/login");
//            return;
//        }
//
//        String username = (String) session.getAttribute("username");
//        String action = req.getParameter("action");
//
//        // If no action, default to the main dashboard
//        if (action == null || action.isEmpty()) {
//            showDashboard(req, resp);
//            return;
//        }
//
//        switch (action) {
//            case "viewPassbook":
//                showPassbookPage(req, resp);
//                break;
//            case "downloadPassbook":
//                downloadPassbookAsPdf(req, resp);
//                break;
//            default:
//                showDashboard(req, resp);
//        }
//        // Fetch all data needed for the dashboard using the logged-in username.
//        CustomerDashboardDTO dashboardData = homeService.getCustomerDashboardData(username);
//
//        if (dashboardData == null) {
//            session.invalidate();
//            resp.sendRedirect(req.getContextPath() + "/login?error=userNotFound");
//            return;
//        }
//
//        req.setAttribute("dashboard", dashboardData);
//        RequestDispatcher dispatcher = req.getRequestDispatcher("/Views/CustomerHome.jsp");
//        dispatcher.forward(req, resp);
//    }
//
//    @Override
//    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
//        HttpSession session = req.getSession(false);
//
//        // Same security check for POST actions to prevent unauthorized operations.
//        if (session == null || session.getAttribute("username") == null) {
//            resp.sendRedirect(req.getContextPath() + "/login");
//            return;
//        }
//
//
//        String username = (String) session.getAttribute("username"); // Get username for password check
//        String action = req.getParameter("action");
//        long userAccountNumber = Long.parseLong(req.getParameter("userAccountNumber"));
//        String password = req.getParameter("password"); // Get password from the form
//
//        try {
//            boolean success = false;
//            switch (action) {
//                case "performCredit":
//                    long creditAmount = (long) (Double.parseDouble(req.getParameter("amount")) * 100);
//                    success = transactionService.performCredit(userAccountNumber, creditAmount, "Customer Deposit", username, password);
//                    if (success) session.setAttribute("successMessage", "Deposit successful!");
//                    break;
//                case "performDebit":
//                    long debitAmount = (long) (Double.parseDouble(req.getParameter("amount")) * 100);
//                    success = transactionService.performDebit(userAccountNumber, debitAmount, "Customer Withdrawal", username, password);
//                    if (success) session.setAttribute("successMessage", "Withdrawal successful!");
//                    break;
//                case "performTransfer":
//                    long transferAmount = (long) (Double.parseDouble(req.getParameter("amount")) * 100);
//                    long receiverAccount = Long.parseLong(req.getParameter("receiverAccount"));
//                    String details = req.getParameter("details");
//                    success = transactionService.performTransfer(userAccountNumber, receiverAccount, transferAmount, details, username, password);
//                    if (success) session.setAttribute("successMessage", "Transfer successful!");
//                    break;
//            }
//            if (!success && session.getAttribute("successMessage") == null) {
//                session.setAttribute("errorMessage", "Transaction failed. Please try again.");
//            }
//        } catch (SQLException | SecurityException | NumberFormatException e) { // Catch SecurityException now
//            session.setAttribute("errorMessage", "Transaction failed: " + e.getMessage());
//            e.printStackTrace();
//        }
//
//        // Redirect back to the homepage to show updated data and messages (PRG Pattern).
//        resp.sendRedirect(req.getContextPath() + "/customer/home");
//    }
//
//    private void showDashboard(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
//        // This is your original doGet logic
//        String username = (String) req.getSession().getAttribute("username");
//        CustomerDashboardDTO dashboardData = homeService.getCustomerDashboardData(username);
//        req.setAttribute("dashboard", dashboardData);
//        RequestDispatcher dispatcher = req.getRequestDispatcher("/Views/CustomerHome.jsp");
//        dispatcher.forward(req, resp);
//    }
//
//    private void showPassbookPage(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
//        String username = (String) req.getSession().getAttribute("username");
//
//        Map<String, String> filters = new HashMap<>();
//        filters.put("startDate", req.getParameter("startDate"));
//        filters.put("endDate", req.getParameter("endDate"));
//        filters.put("txnType", req.getParameter("txnType"));
//
//        UserDAOImpl usersDAO = new UserDAOImpl(dataSource);
//        User customer = usersDAO.getUserByUsername(username); // Reusing DAO
//        List<TransactionDTO> transactionList = homeService.getCustomerTransactions(username, filters);
//
//        req.setAttribute("customer", customer);
//        req.setAttribute("transactionList", transactionList);
//
//        RequestDispatcher dispatcher = req.getRequestDispatcher("/Views/Passbook.jsp");
//        dispatcher.forward(req, resp);
//    }
//
//    private void downloadPassbookAsPdf(HttpServletRequest req, HttpServletResponse resp) throws IOException {
//        String username = (String) req.getSession().getAttribute("username");
//
//        Map<String, String> filters = new HashMap<>();
//        filters.put("startDate", req.getParameter("startDate"));
//        filters.put("endDate", req.getParameter("endDate"));
//        filters.put("txnType", req.getParameter("txnType"));
//        UserDAOImpl usersDAO = new UserDAOImpl(dataSource);
//        User customer = usersDAO.getUserByUsername(username);
//        List<TransactionDTO> transactionList = homeService.getCustomerTransactions(username, filters);
//
//        // --- PDF Generation Logic ---
//        ByteArrayOutputStream baos = new ByteArrayOutputStream();
//        PdfWriter writer = new PdfWriter(baos);
//        PdfDocument pdf = new PdfDocument(writer);
//        Document document = new Document(pdf);
//
//        // 1. Add Header
//        document.add(new Paragraph("Bank Passbook").setBold().setFontSize(20).setTextAlignment(TextAlignment.CENTER));
//        document.add(new Paragraph("Aurionpro Bank").setTextAlignment(TextAlignment.CENTER));
//
//        // 2. Add Account Details
//        document.add(new Paragraph("\nAccount Holder: " + customer.getUsername()).setFontSize(12));
//        document.add(new Paragraph("Account Number: " + customer.getAccountNumber()).setFontSize(12));
//        document.add(new Paragraph("Statement Date: " + java.time.LocalDate.now()).setFontSize(10));
//        document.add(new Paragraph("\n"));
//
//        // 3. Create and Add Transaction Table
//        Table table = new Table(UnitValue.createPercentArray(new float[]{3, 5, 3, 3, 3}));
//        table.setWidth(UnitValue.createPercentValue(100));
//
//        // Table Headers
//        table.addHeaderCell(new Cell().add(new Paragraph("Date").setBold()));
//        table.addHeaderCell(new Cell().add(new Paragraph("Details").setBold()));
//        table.addHeaderCell(new Cell().add(new Paragraph("Credit").setBold()).setTextAlignment(TextAlignment.RIGHT));
//        table.addHeaderCell(new Cell().add(new Paragraph("Debit").setBold()).setTextAlignment(TextAlignment.RIGHT));
//        table.addHeaderCell(new Cell().add(new Paragraph("Balance").setBold()).setTextAlignment(TextAlignment.RIGHT));
//
//        // Table Body - we need to iterate in reverse to calculate balance correctly
//        long runningBalance = customer.getBalance();
//
//        for (TransactionDTO txn : transactionList) {
//            boolean isCredit = (txn.getType() == TXN_TYPE.CREDIT) || (txn.getType() == TXN_TYPE.TRANSFER && txn.getReceiverAccountNumber() == customer.getAccountNumber());
//
//            table.addCell(txn.getTxnDate().format(DateTimeFormatter.ofPattern("dd-MM-yyyy")));
//
//            // Transaction Details cell
//            String details;
//            if (txn.getType() == TXN_TYPE.TRANSFER) {
//                details = isCredit ? "From: " + txn.getSenderName() : "To: " + txn.getReceiverName();
//            } else {
//                details = txn.getDetails();
//            }
//            table.addCell(details);
//
//            // Credit/Debit cells
//            if (isCredit) {
//                table.addCell(new Cell().add(new Paragraph(String.format("%,.2f", txn.getAmount() / 100.0))).setTextAlignment(TextAlignment.RIGHT));
//                table.addCell(""); // Empty Debit cell
//            } else {
//                table.addCell(""); // Empty Credit cell
//                table.addCell(new Cell().add(new Paragraph(String.format("%,.2f", txn.getAmount() / 100.0))).setTextAlignment(TextAlignment.RIGHT));
//            }
//
//            // Balance cell
//            table.addCell(new Cell().add(new Paragraph(String.format("%,.2f", runningBalance / 100.0))).setTextAlignment(TextAlignment.RIGHT));
//
//            // Update running balance for the next older transaction
//            runningBalance = isCredit ? runningBalance - txn.getAmount() : runningBalance + txn.getAmount();
//        }
//
//        document.add(table);
//        document.close();
//
//        // 4. Set Response Headers to Trigger Download
//        resp.setContentType("application/pdf");
//        resp.setHeader("Content-Disposition", "attachment; filename=\"Passbook-" + customer.getAccountNumber() + ".pdf\"");
//        resp.setContentLength(baos.size());
//
//        // 5. Write PDF to Response
//        baos.writeTo(resp.getOutputStream());
//    }
//}

package com.aurionpro.bank_application.Controllers;

import com.aurionpro.bank_application.DAO.TransactionDTO;
import com.aurionpro.bank_application.DAO.UserDAOImpl;
import com.aurionpro.bank_application.ENUMS.TXN_TYPE;
import com.aurionpro.bank_application.Models.User;
import com.aurionpro.bank_application.Services.HomeService;
import com.aurionpro.bank_application.Services.TransactionService;
import com.aurionpro.bank_application.DAO.CustomerDashboardDTO;
import com.itextpdf.io.source.ByteArrayOutputStream;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;
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
import java.sql.SQLException;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@WebServlet("/customer/home")
public class HomeController extends HttpServlet {

    @Resource(name = "jdbc/bank_app")
    private DataSource dataSource;

    private HomeService homeService;
    private TransactionService transactionService;

    @Override
    public void init() throws ServletException {
        super.init();
        homeService = new HomeService(dataSource);
        transactionService = new TransactionService(dataSource);
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession(false);

        if (session == null || session.getAttribute("username") == null) {
            resp.sendRedirect(req.getContextPath() + "/login");
            return; // Important: Stop execution if not logged in
        }

        String action = req.getParameter("action");

        // Use "dashboard" as the default action if none is provided.
        if (action == null || action.isEmpty()) {
            action = "dashboard";
        }

        try {
            switch (action) {
                case "viewPassbook":
                    showPassbookPage(req, resp);
                    break;
                case "downloadPassbook":
                    downloadPassbookAsPdf(req, resp);
                    break;
                default: // Handles "dashboard" and any other unknown action
                    showDashboard(req, resp);
                    break;
            }
        } catch (Exception e) {
            // A general error handler
            e.printStackTrace();
            session.setAttribute("errorMessage", "An unexpected error occurred: " + e.getMessage());
            resp.sendRedirect(req.getContextPath() + "/customer/home");
        }
    }


    private void showDashboard(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String username = (String) req.getSession().getAttribute("username");
        CustomerDashboardDTO dashboardData = homeService.getCustomerDashboardData(username);

        if (dashboardData == null) {
            req.getSession().invalidate();
            resp.sendRedirect(req.getContextPath() + "/login?error=userNotFound");
            return;
        }

        req.setAttribute("dashboard", dashboardData);
        RequestDispatcher dispatcher = req.getRequestDispatcher("/Views/CustomerHome.jsp");
        dispatcher.forward(req, resp);
    }

    private void showPassbookPage(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String username = (String) req.getSession().getAttribute("username");

        // Collect filter parameters from the request
        Map<String, String> filters = new HashMap<>();
        filters.put("startDate", req.getParameter("startDate"));
        filters.put("endDate", req.getParameter("endDate"));
        filters.put("txnType", req.getParameter("txnType"));

        UserDAOImpl usersDAO = new UserDAOImpl(dataSource);
        User customer = usersDAO.getUserByUsername(username);
        // Assuming getCustomerTransactions returns transactions ordered from newest to oldest
        List<TransactionDTO> transactionList = homeService.getCustomerTransactions(username, filters);

        // Set the necessary attributes for the JSP
        req.setAttribute("customer", customer); // The current user's details
        req.setAttribute("transactionList", transactionList);

        RequestDispatcher dispatcher = req.getRequestDispatcher("/Views/Passbook.jsp");
        dispatcher.forward(req, resp);
    }

    // doPost method remains the same as it was already well-structured
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession(false);

        if (session == null || session.getAttribute("username") == null) {
            resp.sendRedirect(req.getContextPath() + "/login");
            return;
        }

        String action = req.getParameter("action");
        if (action == null) {
            resp.sendRedirect(req.getContextPath() + "/customer/home");
            return;
        }

        try {
            String username = (String) session.getAttribute("username");
            String password = req.getParameter("password");
            long userAccountNumber = Long.parseLong(req.getParameter("userAccountNumber"));
            boolean success = false;

            switch (action) {
                case "performCredit":
                    long creditAmount = (long) (Double.parseDouble(req.getParameter("amount")) * 100);
                    success = transactionService.performCredit(userAccountNumber, creditAmount, "Customer Deposit", username, password);
                    if (success) session.setAttribute("successMessage", "Deposit successful!");
                    break;
                case "performDebit":
                    long debitAmount = (long) (Double.parseDouble(req.getParameter("amount")) * 100);
                    success = transactionService.performDebit(userAccountNumber, debitAmount, "Customer Withdrawal", username, password);
                    if (success) session.setAttribute("successMessage", "Withdrawal successful!");
                    break;
                case "performTransfer":
                    long transferAmount = (long) (Double.parseDouble(req.getParameter("amount")) * 100);
                    long receiverAccount = Long.parseLong(req.getParameter("receiverAccount"));
                    String details = req.getParameter("details");
                    success = transactionService.performTransfer(userAccountNumber, receiverAccount, transferAmount, details, username, password);
                    if (success) session.setAttribute("successMessage", "Transfer successful!");
                    break;
            }

            if (!success && session.getAttribute("successMessage") == null) {
                // An error message should have been set by the service layer, but as a fallback:
                if (session.getAttribute("errorMessage") == null) {
                    session.setAttribute("errorMessage", "Transaction failed. Please check details and try again.");
                }
            }
        } catch (SQLException | SecurityException | IllegalArgumentException e) {
            session.setAttribute("errorMessage", "Transaction failed: " + e.getMessage());
            e.printStackTrace();
        }

        resp.sendRedirect(req.getContextPath() + "/customer/home");
    }


    private void downloadPassbookAsPdf(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String username = (String) req.getSession().getAttribute("username");

        Map<String, String> filters = new HashMap<>();
        filters.put("startDate", req.getParameter("startDate"));
        filters.put("endDate", req.getParameter("endDate"));
        filters.put("txnType", req.getParameter("txnType"));

        UserDAOImpl usersDAO = new UserDAOImpl(dataSource);
        User customer = usersDAO.getUserByUsername(username);
        List<TransactionDTO> transactionList = homeService.getCustomerTransactions(username, filters);

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PdfDocument pdf = new PdfDocument(new PdfWriter(baos));
        Document document = new Document(pdf);

        document.add(new Paragraph("Bank Passbook").setBold().setFontSize(20).setTextAlignment(TextAlignment.CENTER));
        document.add(new Paragraph("Aurionpro Bank").setTextAlignment(TextAlignment.CENTER));
        document.add(new Paragraph("\n"));

        document.add(new Paragraph("Account Holder: " + customer.getUsername()).setFontSize(12));
        document.add(new Paragraph("Account Number: " + customer.getAccountNumber()).setFontSize(12));
        document.add(new Paragraph("Statement Date: " + java.time.LocalDate.now()).setFontSize(10));
        document.add(new Paragraph("\n"));

        Table table = new Table(UnitValue.createPercentArray(new float[]{3, 5, 3, 3, 3}));
        table.setWidth(UnitValue.createPercentValue(100));

        table.addHeaderCell(new Cell().add(new Paragraph("Date").setBold()));
        table.addHeaderCell(new Cell().add(new Paragraph("Details").setBold()));
        table.addHeaderCell(new Cell().add(new Paragraph("Debit (-)").setBold()).setTextAlignment(TextAlignment.RIGHT));
        table.addHeaderCell(new Cell().add(new Paragraph("Credit (+)")).setBold()).setTextAlignment(TextAlignment.RIGHT);
        table.addHeaderCell(new Cell().add(new Paragraph("Balance").setBold()).setTextAlignment(TextAlignment.RIGHT));

        // Start with the current balance and work backward through the (newest-to-oldest) transactions
        long runningBalance = customer.getBalance();

        for (TransactionDTO txn : transactionList) {
            boolean isCredit = (txn.getType() == TXN_TYPE.CREDIT) ||
                    (txn.getType() == TXN_TYPE.TRANSFER && txn.getReceiverAccountNumber() == customer.getAccountNumber());

            // Column 1: Date
            table.addCell(txn.getFormattedTxnDate());

            // Column 2: Details
            String details;
            if (txn.getType() == TXN_TYPE.TRANSFER) {
                details = isCredit ? "From: " + txn.getSenderName() + " (" + txn.getSenderAccountNumber() + ")"
                        : "To: " + txn.getReceiverName() + " (" + txn.getReceiverAccountNumber() + ")";
            } else {
                details = txn.getDetails();
            }
            table.addCell(details);

            // Columns 3 & 4: Debit/Credit
            String amountStr = String.format("%,.2f", txn.getAmount() / 100.0);
            if (isCredit) {
                table.addCell(""); // Empty Debit cell
                table.addCell(new Cell().add(new Paragraph(amountStr)).setTextAlignment(TextAlignment.RIGHT));
            } else {
                table.addCell(new Cell().add(new Paragraph(amountStr)).setTextAlignment(TextAlignment.RIGHT));
                table.addCell(""); // Empty Credit cell
            }

            // Column 5: Balance (This is the balance *after* this transaction occurred)
            table.addCell(new Cell().add(new Paragraph(String.format("%,.2f", runningBalance / 100.0))).setTextAlignment(TextAlignment.RIGHT));

            // Update running balance for the *next older* transaction in the loop
            runningBalance = isCredit ? (runningBalance - txn.getAmount()) : (runningBalance + txn.getAmount());
        }

        document.add(table);
        document.close();

        resp.setContentType("application/pdf");
        resp.setHeader("Content-Disposition", "attachment; filename=\"Passbook-" + customer.getAccountNumber() + ".pdf\"");
        resp.setContentLength(baos.size());

        baos.writeTo(resp.getOutputStream());
    }
}