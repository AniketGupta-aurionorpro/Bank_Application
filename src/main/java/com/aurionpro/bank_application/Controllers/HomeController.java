package com.aurionpro.bank_application.Controllers;

import com.aurionpro.bank_application.DAO.UserDAOImpl;
import com.aurionpro.bank_application.ENUMS.TXN_TYPE;
import com.aurionpro.bank_application.Models.User;
import com.aurionpro.bank_application.Services.HomeService;
import com.aurionpro.bank_application.Services.TransactionDTO;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@WebServlet("/customer/home")
public class HomeController extends HttpServlet {

    @Resource(name = "jdbc/bank_app")
    private DataSource dataSource;

    private HomeService homeService;
    private TransactionDTO transactionService;

    @Override
    public void init() throws ServletException {
        super.init();
        homeService = new HomeService(dataSource);
        transactionService = new TransactionDTO(dataSource);
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession(false);

        if (session == null || session.getAttribute("username") == null) {
            resp.sendRedirect(req.getContextPath() + "/login");
            return;
        }

        String action = req.getParameter("action");

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
                default:
                    showDashboard(req, resp);
                    break;
            }
        } catch (Exception e) {
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


        Map<String, String> filters = new HashMap<>();
        filters.put("startDate", req.getParameter("startDate"));
        filters.put("endDate", req.getParameter("endDate"));
        filters.put("txnType", req.getParameter("txnType"));

        UserDAOImpl usersDAO = new UserDAOImpl(dataSource);
        User customer = usersDAO.getUserByUsername(username);
        List<com.aurionpro.bank_application.DAO.TransactionDTO> transactionList = homeService.getCustomerTransactions(username, filters);

        req.setAttribute("customer", customer);
        req.setAttribute("transactionList", transactionList);

        RequestDispatcher dispatcher = req.getRequestDispatcher("/Views/Passbook.jsp");
        dispatcher.forward(req, resp);
    }

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
        List<com.aurionpro.bank_application.DAO.TransactionDTO> transactionList = homeService.getCustomerTransactions(username, filters);

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


        long runningBalance = customer.getBalance();

        for (com.aurionpro.bank_application.DAO.TransactionDTO txn : transactionList) {
            boolean isCredit = (txn.getType() == TXN_TYPE.CREDIT) ||
                    (txn.getType() == TXN_TYPE.TRANSFER && txn.getReceiverAccountNumber() == customer.getAccountNumber());


            table.addCell(txn.getFormattedTxnDate());


            String details;
            if (txn.getType() == TXN_TYPE.TRANSFER) {
                details = isCredit ? "From: " + txn.getSenderName() + " (" + txn.getSenderAccountNumber() + ")"
                        : "To: " + txn.getReceiverName() + " (" + txn.getReceiverAccountNumber() + ")";
            } else {
                details = txn.getDetails();
            }
            table.addCell(details);


            String amountStr = String.format("%,.2f", txn.getAmount() / 100.0);
            if (isCredit) {
                table.addCell("");
                table.addCell(new Cell().add(new Paragraph(amountStr)).setTextAlignment(TextAlignment.RIGHT));
            } else {
                table.addCell(new Cell().add(new Paragraph(amountStr)).setTextAlignment(TextAlignment.RIGHT));
                table.addCell("");
            }


            table.addCell(new Cell().add(new Paragraph(String.format("%,.2f", runningBalance / 100.0))).setTextAlignment(TextAlignment.RIGHT));


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