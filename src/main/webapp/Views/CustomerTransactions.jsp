<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%--<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>--%>
<%--<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>--%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<!DOCTYPE html>
<html lang="en">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1.0">
  <title>Transaction History - ${customer.username}</title>
  <!-- Include same Bootstrap, Icons, and Font links as AdminDashboard.jsp -->
  <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
  <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.3/font/bootstrap-icons.min.css">
  <link href="https://fonts.googleapis.com/css2?family=Poppins:wght@400;500;600;700&display=swap" rel="stylesheet">

  <style>
    /* Copy the core styles (body, wrapper, sidebar, main-content, card, etc.) from AdminDashboard.jsp for consistency */
    body { font-family: 'Poppins', sans-serif; background-color: #f4f7fa; }
    .wrapper { display: flex; width: 100%; min-height: 100vh; }
    .sidebar { width: 260px; min-width: 260px; background-color: #2c3e50; color: #ecf0f1; transition: margin-left 0.3s ease-in-out; }
    .sidebar.collapsed { margin-left: -260px; }
    /* Add the rest of the sidebar and header CSS here... */
    .main-content { flex-grow: 1; padding: 30px; transition: all 0.3s ease-in-out; }
    .card { border: none; border-radius: 12px; box-shadow: 0 4px 25px rgba(0,0,0,0.08); }
    .card-header { background-color: #fff; border-bottom: 1px solid #f0f0f0; padding: 20px 25px; font-weight: 600; font-size: 1.1rem; }
    .table th { font-weight: 600; color: #6c757d; }
    .table td { vertical-align: middle; }
    .amount-credit { color: #198754; font-weight: 600; }
    .amount-debit { color: #dc3545; font-weight: 600; }
    .sidebar {
      width: 260px;
      min-width: 260px; /* Prevent sidebar from shrinking */
      background-color: #2c3e50; /* Dark blue-gray */
      color: #ecf0f1;
      transition: margin-left 0.3s ease-in-out;
      position: fixed; /* Fix sidebar position */
      height: 100%; /* Make sidebar full height */
      z-index: 1000; /* Ensure sidebar is above other content */
    }
    .sidebar.collapsed {
      margin-left: -260px;
    }

    .sidebar-header {
      padding: 20px;
      text-align: center;
      border-bottom: 1px solid #34495e;
    }

    .sidebar-header h3 {
      margin: 0;
      font-weight: 600;
    }

    .sidebar-nav {
      padding: 0;
      list-style: none;
    }

    .sidebar-nav li {
      padding: 0;
    }

    .sidebar-nav a {
      display: flex;
      align-items: center;
      padding: 15px 20px;
      color: #ecf0f1;
      text-decoration: none;
      transition: background-color 0.2s, color 0.2s;
      font-weight: 500;
    }

    .sidebar-nav a:hover {
      background-color: #34495e;
    }

    .sidebar-nav a.active {
      background-color: #e74c3c; /* A highlight color */
      color: #ffffff;
    }

    .sidebar-nav a i {
      margin-right: 15px;
      font-size: 1.2rem;
      width: 20px;
      text-align: center;
    }

    .sidebar-nav .badge {
      margin-left: auto;
    }
    .main-content {
      flex-grow: 1;
      padding: 30px;
      transition: all 0.3s ease-in-out;
      overflow-x: hidden; /* Prevent horizontal scroll on resize */
      margin-left: 260px; /* Offset for fixed sidebar */
    }

    .main-content.sidebar-collapsed {
      margin-left: 0;
    }

    .main-header {
      display: flex;
      justify-content: space-between;
      align-items: center;
      margin-bottom: 30px;
    }

    .main-header h1 {
      margin: 0;
      font-weight: 700;
    }

    .admin-profile {
      font-weight: 500;
    }
    .sidebar-toggle {
      background: none;
      border: none;
      font-size: 1.5rem;
      color: #2c3e50;
      cursor: pointer;
    }

    .header-left {
      display: flex;
      align-items: center;
      gap: 15px;
    }
    /* Info Cards */
    .info-card {
      background: linear-gradient(45deg, #007bff, #0056b3); /* Blue gradient */
      color: #fff;
      padding: 25px;
      border-radius: 12px;
      text-align: center;
      box-shadow: 0 4px 20px rgba(0, 123, 255, 0.2);
      margin-bottom: 25px;
      display: flex;
      flex-direction: column;
      justify-content: center;
      align-items: center;
      height: 150px; /* Fixed height for consistency */
    }

    .info-card.red {
      background: linear-gradient(45deg, #dc3545, #b02a37); /* Red gradient */
      box-shadow: 0 4px 20px rgba(220, 53, 69, 0.2);
    }

    .info-card h4 {
      font-size: 1.2rem;
      margin-bottom: 10px;
      font-weight: 500;
      opacity: 0.8;
    }

    .info-card .value {
      font-size: 2.5rem;
      font-weight: 700;
      line-height: 1;
    }
  </style>
</head>
<body>
<div class="wrapper">
  <!-- Include the Sidebar navigation from AdminDashboard.jsp -->
  <nav id="sidebar" class="sidebar">
    <div class="sidebar-header">
      <h3>Aurionpro Bank</h3>
    </div>

    <ul class="sidebar-nav">
      <li>
        <a href="${pageContext.request.contextPath}/admin/dashboard" class="active">
          <i class="bi bi-grid-1x2-fill"></i> Dashboard
        </a>
      </li>
      <li>
        <a href="${pageContext.request.contextPath}/addCustomer">
          <i class="bi bi-person-plus-fill"></i> Add Customer
        </a>
      </li>
      <%--            <li>--%>
      <%--                <a href="${pageContext.request.contextPath}/admin?action=showPending">--%>
      <%--                    <i class="bi bi-bell-fill"></i> Notifications--%>
      <%--                    <span class="badge rounded-pill bg-danger">3</span>--%>
      <%--                </a>--%>
      <%--            </li>--%>
      <li> <%-- NEW: View All Transactions link --%>
        <a href="${pageContext.request.contextPath}/admin?action=viewAllTransactions">
          <i class="bi bi-receipt-cutoff"></i> View All Transactions
        </a>
      </li>
      <li>
        <a href="${pageContext.request.contextPath}/logoutServlet">
          <i class="bi bi-box-arrow-left"></i> Logout
        </a>
      </li>
    </ul>
  </nav>

  <main class="main-content">
    <header class="d-flex justify-content-between align-items-center mb-4">
      <div>
        <a href="${pageContext.request.contextPath}/admin/dashboard" class="btn btn-outline-secondary me-3">
          <i class="bi bi-arrow-left"></i> Back to Dashboard
        </a>
      </div>
      <div class="text-end">
        <h1 class="h3 mb-0">Transaction History</h1>
        <p class="mb-0 text-muted">For: <strong>${customer.username}</strong> | Acc No: <strong>${customer.accountNumber}</strong></p>
      </div>
    </header>

    <!-- Filter Card -->
    <div class="card mb-4">
      <div class="card-header">
        <i class="bi bi-funnel-fill"></i> Filters
      </div>
      <div class="card-body">
        <form action="${pageContext.request.contextPath}/admin" method="GET" class="row g-3 align-items-end">
          <input type="hidden" name="action" value="viewTransactions">
          <input type="hidden" name="id" value="${customer.id}">

          <div class="col-md-3">
            <label for="startDate" class="form-label">Start Date</label>
            <input type="date" class="form-control" id="startDate" name="startDate" value="${param.startDate}" max="<fmt:formatDate value="<%=new java.util.Date()%>" pattern="yyyy-MM-dd"/>">
          </div>
          <div class="col-md-3">
            <label for="endDate" class="form-label">End Date</label>
            <input type="date" class="form-control" id="endDate" name="endDate" value="${param.endDate}" max="<fmt:formatDate value="<%=new java.util.Date()%>" pattern="yyyy-MM-dd"/>">
          </div>
          <div class="col-md-2">
            <label for="txnType" class="form-label">Type</label>
            <select id="txnType" name="txnType" class="form-select">
              <option value="" ${empty param.txnType ? 'selected' : ''}>All</option>
              <option value="CREDIT" ${param.txnType == 'CREDIT' ? 'selected' : ''}>Credit</option>
              <option value="DEBIT" ${param.txnType == 'DEBIT' ? 'selected' : ''}>Debit</option>
              <option value="TRANSFER" ${param.txnType == 'TRANSFER' ? 'selected' : ''}>Transfer</option>
            </select>
          </div>
          <div class="col-md-2">
            <label for="receiverAccount" class="form-label">Receiver Account</label>
            <input type="text" class="form-control" id="receiverAccount" name="receiverAccount" value="${param.receiverAccount}" placeholder="e.g., 12345">
          </div>
          <div class="col-md-2">
            <button type="submit" class="btn btn-primary w-100">Apply</button>
          </div>
        </form>
      </div>
    </div>

    <!-- Transactions Table Card -->
    <div class="card">
      <div class="card-body p-0">
        <div class="table-responsive">
          <table class="table table-hover mb-0">
            <thead>
            <tr>
              <th>Sender Account</th>
              <th>Receiver Account</th>
              <th>Receiver Name</th>
              <th>Type</th>
              <th class="text-end">Amount</th>
              <th>Details</th>
              <th>Date & Time</th>
            </tr>
            </thead>
            <tbody>
            <c:choose>
              <c:when test="${not empty transactionList}">
                <c:forEach var="txn" items="${transactionList}">
                  <tr>
                    <td><c:out value="${txn.senderAccountNumber}"/></td>
                    <td>
                      <c:if test="${txn.type == 'TRANSFER'}"><c:out value="${txn.receiverAccountNumber}"/></c:if>
                      <c:if test="${txn.type != 'TRANSFER'}"><span class="text-muted">--</span></c:if>
                    </td>
                    <td>
                      <c:if test="${txn.type == 'TRANSFER'}"><c:out value="${txn.receiverName}"/></c:if>
                      <c:if test="${txn.type != 'TRANSFER'}"><span class="text-muted">--</span></c:if>
                    </td>
                    <td><span class="badge bg-secondary text-uppercase">${txn.type}</span></td>
                    <td class="text-end">
                      <c:set var="isCredit" value="${txn.type == 'CREDIT' && txn.receiverAccountNumber == customer.accountNumber}"/>
                      <c:set var="isDebit" value="${txn.senderAccountNumber == customer.accountNumber && (txn.type == 'DEBIT' || txn.type == 'TRANSFER')}"/>

                      <c:choose>
                        <c:when test="${isCredit}">
                          <span class="amount-credit">+ ₹<fmt:formatNumber value="${txn.amount / 100.0}" type="number" minFractionDigits="2" maxFractionDigits="2"/></span>
                        </c:when>
                        <c:when test="${isDebit}">
                          <span class="amount-debit">- ₹<fmt:formatNumber value="${txn.amount / 100.0}" type="number" minFractionDigits="2" maxFractionDigits="2"/></span>
                        </c:when>
                        <c:otherwise> <!-- It's a transfer where the current user is the receiver -->
                          <span class="amount-credit">+ ₹<fmt:formatNumber value="${txn.amount / 100.0}" type="number" minFractionDigits="2" maxFractionDigits="2"/></span>
                        </c:otherwise>
                      </c:choose>
                    </td>
                    <td><c:out value="${txn.details}"/></td>
                    <td><c:out value="${txn.formattedTxnDate}"/></td>
                  </tr>
                </c:forEach>
              </c:when>
              <c:otherwise>
                <tr>
                  <td colspan="7" class="text-center p-5">No transactions found for the selected criteria.</td>
                </tr>
              </c:otherwise>
            </c:choose>
            </tbody>
          </table>
        </div>
      </div>
    </div>
  </main>
</div>
<!-- Add Bootstrap JS Bundle and sidebar toggle script -->
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>
<script>
  document.addEventListener("DOMContentLoaded", function() {
    const sidebar = document.getElementById('sidebar');
    const mainContent = document.getElementById('main-content'); // Get main content
    const toggleButton = document.getElementById('sidebar-toggle');

    if (toggleButton) {
      toggleButton.addEventListener('click', function() {
        sidebar.classList.toggle('collapsed');
        mainContent.classList.toggle('sidebar-collapsed'); // Toggle class on main content
      });
    }
  });
</script>
</body>
</html>