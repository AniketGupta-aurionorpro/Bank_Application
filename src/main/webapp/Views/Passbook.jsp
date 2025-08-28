<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html lang="en">
<head>
  <meta charset="UTF-8">
  <title>Bank Passbook</title>
  <!-- CSS Links remain the same -->
  <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
  <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.3/font/bootstrap-icons.min.css">
  <link href="https://fonts.googleapis.com/css2?family=Poppins:wght@400;500;600;700&display=swap" rel="stylesheet">
  <style>
    /* CSS styles remain the same */
    body { font-family: 'Poppins', sans-serif; background-color: #f4f7fa; }
    .wrapper { display: flex; width: 100%; min-height: 100vh; }
    .sidebar { width: 260px; min-width: 260px; background-color: #2c3e50; color: #ecf0f1; transition: margin-left 0.3s ease-in-out; }
    .main-content { flex-grow: 1; padding: 30px; }
    .card { border: none; border-radius: 12px; box-shadow: 0 4px 25px rgba(0,0,0,0.08); margin-bottom: 25px; }
    .card-header { background-color: #fff; border-bottom: 1px solid #f0f0f0; padding: 20px 25px; font-weight: 600; font-size: 1.1rem; }
    .sidebar-header h3 { margin: 0; font-weight: 600; }
    .sidebar-nav { padding: 0; list-style: none; }
    .sidebar-nav a { display: flex; align-items: center; padding: 15px 20px; color: #ecf0f1; text-decoration: none; transition: background-color 0.2s; font-weight: 500; }
    .sidebar-nav a.active, .sidebar-nav a:hover { background-color: #34495e; }
    .sidebar-nav a i { margin-right: 15px; font-size: 1.2rem; }
    .info-card { padding: 25px; text-align: center; }
    .info-card h4 { font-size: 1.1rem; margin-bottom: 10px; font-weight: 500; color: #6c757d; }
    .info-card .value { font-size: 2.2rem; font-weight: 700; line-height: 1; color: #2c3e50; }
    .amount-credit { color: #198754; font-weight: 600; }
    .amount-debit { color: #dc3545; font-weight: 600; }
  </style>
</head>
<body>
<div class="wrapper">
  <!-- Sidebar -->
  <nav id="sidebar" class="sidebar">
    <div class="sidebar-header p-3 text-center border-bottom"><h3>Aurionpro Bank</h3></div>
    <ul class="sidebar-nav">
      <li><a href="${pageContext.request.contextPath}/customer/home"><i class="bi bi-grid-1x2-fill"></i> Dashboard</a></li>
      <li><a href="${pageContext.request.contextPath}/customer/home?action=viewPassbook" class="active"><i class="bi bi-book-fill"></i> Passbook</a></li>
      <li><a href="#" data-bs-toggle="modal" data-bs-target="#transferModal"><i class="bi bi-send-fill"></i> Make a Transfer</a></li>
      <li><a href="${pageContext.request.contextPath}/logoutServlet"><i class="bi bi-box-arrow-left"></i> Logout</a></li>
    </ul>
  </nav>

  <!-- Main Content -->
  <main class="main-content">
    <header class="d-flex justify-content-between align-items-center mb-4">
      <h1 class="h3 mb-0">My Passbook</h1>
      <div>
        <a href="${pageContext.request.contextPath}/customer/home?action=downloadPassbook&startDate=${param.startDate}&endDate=${param.endDate}&txnType=${param.txnType}"
           class="btn btn-danger">
          <i class="bi bi-file-earmark-pdf-fill"></i> Download as PDF
        </a>
      </div>
    </header>

    <!-- Filter Card -->
    <div class="card mb-4">
      <div class="card-header"><i class="bi bi-funnel-fill"></i> Filter Transactions</div>
      <div class="card-body">
        <form action="${pageContext.request.contextPath}/customer/home" method="GET" class="row g-3 align-items-end">
          <input type="hidden" name="action" value="viewPassbook">
          <div class="col-md-4"><label for="startDate" class="form-label">Start Date</label><input type="date" class="form-control" id="startDate" name="startDate" value="${param.startDate}"></div>
          <div class="col-md-4"><label for="endDate" class="form-label">End Date</label><input type="date" class="form-control" id="endDate" name="endDate" value="${param.endDate}"></div>
          <div class="col-md-2"><label for="txnType" class="form-label">Type</label><select id="txnType" name="txnType" class="form-select"><option value="" ${empty param.txnType ? 'selected' : ''}>All</option><option value="CREDIT" ${param.txnType == 'CREDIT' ? 'selected' : ''}>Credit</option><option value="DEBIT" ${param.txnType == 'DEBIT' ? 'selected' : ''}>Debit</option><option value="TRANSFER" ${param.txnType == 'TRANSFER' ? 'selected' : ''}>Transfer</option></select></div>
          <div class="col-md-2"><button type="submit" class="btn btn-primary w-100">Apply Filter</button></div>
        </form>
      </div>
    </div>

    <!-- Transaction Table -->
    <div class="card">
      <div class="card-body p-0">
        <div class="table-responsive">
          <table class="table table-hover mb-0">
            <thead>
            <tr>
              <th>Date & Time</th>
              <th>Details</th>
              <th class="text-end">Amount</th>
              <th class="text-end">Balance After Txn</th>
            </tr>
            </thead>
            <tbody>
            <%-- Initialize running balance with the customer's CURRENT total balance --%>
            <c:set var="runningBalance" value="${customer.balance}"/>

            <c:forEach var="txn" items="${transactionList}">
              <%-- Determine if the transaction is a credit for the current user --%>
              <%-- FIX: Changed dashboard.customer.accountNumber to customer.accountNumber --%>
              <c:set var="isCredit" value="${(txn.type == 'CREDIT') || (txn.type == 'TRANSFER' && txn.receiverAccountNumber == customer.accountNumber)}"/>

              <tr>
                <td>${txn.formattedTxnDate}</td>
                <td>
                  <c:choose>
                    <%-- FIX: Changed dashboard.customer.accountNumber to customer.accountNumber --%>
                    <c:when test="${txn.type == 'TRANSFER' && txn.senderAccountNumber == customer.accountNumber}">
                      Sent to: <strong>${txn.receiverName}</strong> (${txn.receiverAccountNumber})
                    </c:when>
                    <c:when test="${txn.type == 'TRANSFER' && txn.receiverAccountNumber == customer.accountNumber}">
                      Received from: <strong>${txn.senderName}</strong> (${txn.senderAccountNumber})
                    </c:when>
                    <c:otherwise>
                      <c:out value="${txn.details}"/>
                    </c:otherwise>
                  </c:choose>
                </td>
                <td class="text-end">
                  <c:choose>
                    <c:when test="${isCredit}">
                      <span class="amount-credit">+ ₹<fmt:formatNumber value="${txn.amount / 100.0}" type="number" minFractionDigits="2"/></span>
                    </c:when>
                    <c:otherwise>
                      <span class="amount-debit">- ₹<fmt:formatNumber value="${txn.amount / 100.0}" type="number" minFractionDigits="2"/></span>
                    </c:otherwise>
                  </c:choose>
                </td>
                <td class="text-end fw-bold">
                    <%-- This displays the balance as it was AFTER this specific transaction --%>
                  ₹<fmt:formatNumber value="${runningBalance / 100.0}" type="number" minFractionDigits="2"/>
                </td>
              </tr>

              <%-- After displaying the row, update the running balance for the NEXT older transaction --%>
              <c:choose>
                <c:when test="${isCredit}">
                  <c:set var="runningBalance" value="${runningBalance - txn.amount}"/>
                </c:when>
                <c:otherwise>
                  <c:set var="runningBalance" value="${runningBalance + txn.amount}"/>
                </c:otherwise>
              </c:choose>
            </c:forEach>
            <c:if test="${empty transactionList}">
              <tr><td colspan="4" class="text-center p-4">No transactions found for the selected criteria.</td></tr>
            </c:if>
            </tbody>
          </table>
        </div>
      </div>
    </div>
  </main>
</div>

<!-- Transfer Modal - remains the same, but ensure userAccountNumber is passed correctly from dashboard/customer -->
<div class="modal fade" id="transferModal" tabindex="-1">
  <div class="modal-dialog">
    <div class="modal-content">
      <div class="modal-header"><h5 class="modal-title">Transfer Funds</h5><button type="button" class="btn-close" data-bs-dismiss="modal"></button></div>
      <form id="transferForm" action="${pageContext.request.contextPath}/customer/home" method="POST">
        <div class="modal-body">
          <input type="hidden" name="action" value="performTransfer">
          <%-- Note: This 'customer' object is only available on the Passbook page.
               If this modal is used on CustomerHome.jsp, it should use 'dashboard.customer'.
               It's better to pass this from the logged-in user's session if possible.
               Let's assume this modal is mainly for the dashboard.
          --%>
          <input type="hidden" name="userAccountNumber" value="${customer.accountNumber != null ? customer.accountNumber : dashboard.customer.accountNumber}">
          <div class="mb-3">
            <label for="receiverAccount" class="form-label">Receiver's Account Number</label>
            <input type="number" class="form-control" id="receiverAccount" name="receiverAccount" required>
          </div>
          <div class="mb-3">
            <label for="amount" class="form-label">Amount (₹)</label>
            <input type="number" step="0.01" class="form-control" id="amount" name="amount" required>
          </div>
          <div class="mb-3">
            <label for="details" class="form-label">Details (Optional)</label>
            <textarea class="form-control" id="details" name="details" rows="2"></textarea>
          </div>
          <div class="mb-3">
            <label for="receiverName" class="form-label">Receiver's Name</label>
            <input type="text" class="form-control" id="receiverName" readonly style="background-color: #e9ecef;">
            <div id="receiverError" class="text-danger small mt-1"></div>
          </div>
          <div class="mb-3">
            <label for="transferPassword" class="form-label">Confirm with Password</label>
            <input type="password" class="form-control" id="transferPassword" name="password" required>
          </div>
        </div>
        <div class="modal-footer"><button type="submit" id="transferSubmitBtn" class="btn btn-primary" disabled>Send Money</button></div>
      </form>
    </div>
  </div>
</div>

<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>
<script>
  // Your JavaScript remains the same, it looks good.
  const contextPath = '${pageContext.request.contextPath}';
  document.addEventListener('DOMContentLoaded', function() {
    const receiverAccountInput = document.getElementById('receiverAccount');
    const receiverNameInput = document.getElementById('receiverName');
    const receiverErrorDiv = document.getElementById('receiverError');
    const transferSubmitBtn = document.getElementById('transferSubmitBtn');
    let debounceTimer;
    receiverAccountInput.addEventListener('input', function() {
      clearTimeout(debounceTimer);
      receiverNameInput.value = '';
      receiverErrorDiv.textContent = '';
      transferSubmitBtn.disabled = true;
      const accountNumber = this.value;
      debounceTimer = setTimeout(() => {
        if (accountNumber && accountNumber.length >= 8) {
          verifyAccount(accountNumber);
        }
      }, 500);
    });
    async function verifyAccount(accountNumber) {
      try {
        receiverNameInput.value = 'Verifying...';
        const response = await fetch(`${contextPath}/verify-account?accountNumber=${accountNumber}`);
        if (!response.ok) { throw new Error('Network response was not ok'); }
        const data = await response.json();
        if (data.isValid) {
          receiverNameInput.value = data.receiverName;
          receiverErrorDiv.textContent = '';
          transferSubmitBtn.disabled = false;
        } else {
          receiverNameInput.value = '';
          receiverErrorDiv.textContent = data.message || 'Account not found.';
          transferSubmitBtn.disabled = true;
        }
      } catch (error) {
        console.error('Fetch error:', error);
        receiverNameInput.value = '';
        receiverErrorDiv.textContent = 'Error verifying account. Please try again.';
        transferSubmitBtn.disabled = true;
      }
    }
  });
</script>

</body>
</html>