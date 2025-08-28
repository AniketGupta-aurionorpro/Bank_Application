<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Customer Dashboard</title>
    <!-- Reusing the same styles as the Admin Dashboard -->
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.3/font/bootstrap-icons.min.css">
    <link href="https://fonts.googleapis.com/css2?family=Poppins:wght@400;500;600;700&display=swap" rel="stylesheet">
    <style>
        /* --- Paste the exact same CSS from AdminDashboard.jsp here --- */
        /* It includes body, .wrapper, .sidebar, .main-content, .card, etc. */
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
        .amount-credit {
            color: green;
            font-weight: bold;
        }
        .amount-debit {
            color: red;
            font-weight: bold;
        }
    </style>
</head>
<body>
<div class="wrapper">
    <!-- Sidebar -->
    <nav id="sidebar" class="sidebar">
        <div class.sidebar-header p-3 text-center border-bottom><h3>Aurionpro Bank</h3></div>
        <ul class="sidebar-nav">
            <li><a href="${pageContext.request.contextPath}/customer/home" class="active"><i class="bi bi-grid-1x2-fill"></i> Dashboard</a></li>
            <li><a href="#" data-bs-toggle="modal" data-bs-target="#transferModal"><i class="bi bi-send-fill"></i> Make a Transfer</a></li>
            <li><a href="${pageContext.request.contextPath}/customer/home?action=viewPassbook"><i class="bi bi-book-fill"></i> Passbook</a></li>
            <li><a href="${pageContext.request.contextPath}/logoutServlet"><i class="bi bi-box-arrow-left"></i> Logout</a></li>
        </ul>
    </nav>

    <!-- Main Content -->
    <main class="main-content">
        <!-- Header -->
        <header class="d-flex justify-content-between align-items-center mb-4">
            <h1 class="h3 mb-0">Dashboard</h1>
            <div class="admin-profile">
                Welcome, <strong><c:out value="${sessionScope.username}"/></strong>
            </div>
        </header>

        <!-- Flash Messages for Success/Error -->
        <c:if test="${not empty sessionScope.successMessage}">
            <div class="alert alert-success alert-dismissible fade show" role="alert">
                    ${sessionScope.successMessage}
                <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
            </div>
            <c:remove var="successMessage" scope="session"/>
        </c:if>
        <c:if test="${not empty sessionScope.errorMessage}">
            <div class="alert alert-danger alert-dismissible fade show" role="alert">
                    ${sessionScope.errorMessage}
                <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
            </div>
            <c:remove var="errorMessage" scope="session"/>
        </c:if>

        <!-- Account Overview Cards -->
        <div class="row">
            <div class="col-md-6">
                <div class="card info-card">
                    <h4>Current Balance</h4>
                    <div class="value">₹<fmt:formatNumber value="${dashboard.customer.balance / 100.0}" type="number" minFractionDigits="2" maxFractionDigits="2"/></div>
                </div>
            </div>
            <div class="col-md-6">
                <div class="card info-card">
                    <h4>Account Number</h4>
                    <div class="value">${dashboard.customer.accountNumber}</div>
                </div>
            </div>
        </div>

        <!-- Quick Actions -->
        <div class="card">
            <div class="card-header">Quick Actions</div>
            <div class="card-body text-center">
                <button class="btn btn-success btn-lg m-2" data-bs-toggle="modal" data-bs-target="#creditModal"><i class="bi bi-plus-circle"></i> Deposit</button>
                <button class="btn btn-danger btn-lg m-2" data-bs-toggle="modal" data-bs-target="#debitModal"><i class="bi bi-dash-circle"></i> Withdraw</button>
                <button class="btn btn-primary btn-lg m-2" data-bs-toggle="modal" data-bs-target="#transferModal"><i class="bi bi-send-fill"></i> Transfer</button>
            </div>
        </div>

        <!-- Recent Transactions -->
        <div class="card">
            <div class="card-header">Recent Transactions</div>
            <div class="card-body p-0">
                <div class="table-responsive">
                    <table class="table table-hover mb-0">
                        <thead>
                        <tr>
                            <th>Type</th>
                            <th>Details</th>
                            <th class="text-end">Amount</th>
                            <th class="text-end">Balance After</th>
                            <th>Date</th>
                        </tr>
                        </thead>
                        <tbody>
                        <c:set var="runningBalance" value="${dashboard.customer.balance}" />
                        <c:forEach var="txn" items="${dashboard.recentTransactions}" varStatus="loop" begin="0" end="4"> <!-- Show latest 5 -->
                            <tr>
                                <td><span class="badge bg-secondary text-uppercase">${txn.type}</span></td>
                                <td>
                                    <c:choose>
                                        <c:when test="${txn.type == 'TRANSFER' && txn.senderAccountNumber == dashboard.customer.accountNumber}">
                                            Sent to: ${txn.receiverName} (${txn.receiverAccountNumber})
                                        </c:when>
                                        <c:when test="${txn.type == 'TRANSFER' && txn.receiverAccountNumber == dashboard.customer.accountNumber}">
                                            Received from: ${txn.senderName} (${txn.senderAccountNumber})
                                        </c:when>
                                        <c:otherwise>
                                            <c:out value="${txn.details}"/>
                                        </c:otherwise>
                                    </c:choose>
                                </td>
                                <</td>
                                <td class="text-end">
                                    <c:set var="isCredit" value="${(txn.type == 'CREDIT') || (txn.type == 'TRANSFER' && txn.receiverAccountNumber == dashboard.customer.accountNumber)}"/>
                                    <c:choose>
                                        <c:when test="${isCredit}">
                                            <span class="amount-credit">+ ₹<fmt:formatNumber value="${txn.amount / 100.0}" type="number" minFractionDigits="2"/></span>
                                        </c:when>
                                        <c:otherwise>
                                            <span class="amount-debit">- ₹<fmt:formatNumber value="${txn.amount / 100.0}" type="number" minFractionDigits="2"/></span>
                                        </c:otherwise>
                                    </c:choose>
                                </td>
                                <!-- Display the running balance for this row -->
                                <td class="text-end">
                                    ₹<fmt:formatNumber value="${runningBalance / 100.0}" type="number" minFractionDigits="2"/>
                                </td>
                                <td>${txn.formattedTxnDate}</td>
                            </tr>
                            <c:choose>
                                <c:when test="${isCredit}">
                                    <c:set var="runningBalance" value="${runningBalance - txn.amount}"/>
                                </c:when>
                                <c:otherwise>
                                    <c:set var="runningBalance" value="${runningBalance + txn.amount}"/>
                                </c:otherwise>
                            </c:choose>
                        </c:forEach>
                        </tbody>
                    </table>
                </div>
            </div>
        </div>
    </main>
</div>

<!-- Modals for Quick Actions -->
<!-- Credit Modal -->
<div class="modal fade" id="creditModal" tabindex="-1">
    <div class="modal-dialog">
        <div class="modal-content">
            <div class="modal-header"><h5 class="modal-title">Make a Deposit</h5><button type="button" class="btn-close" data-bs-dismiss="modal"></button></div>
            <form action="${pageContext.request.contextPath}/customer/home" method="POST">
                <div class="modal-body">
                    <!-- FIX: Added missing hidden fields and amount input -->
                    <input type="hidden" name="action" value="performCredit">
                    <input type="hidden" name="userAccountNumber" value="${dashboard.customer.accountNumber}">
                    <div class="mb-3">
                        <label for="creditAmount" class="form-label">Amount</label>
                        <input type="number" class="form-control" id="creditAmount" name="amount" step="0.01" min="0.01" required>
                    </div>
                    <!-- Password field is correct -->
                    <div class="mb-3">
                        <label for="creditPassword" class="form-label">Confirm with Password</label>
                        <input type="password" class="form-control" id="creditPassword" name="password" required>
                    </div>
                </div>
                <div class="modal-footer"><button type="submit" class="btn btn-primary">Deposit</button></div>
            </form>
        </div>
    </div>
</div>
<!-- Debit Modal -->
<div class="modal fade" id="debitModal" tabindex="-1">
    <div class="modal-dialog">
        <div class="modal-content">
            <div class="modal-header"><h5 class="modal-title">Make a Withdrawal</h5><button type="button" class="btn-close" data-bs-dismiss="modal"></button></div>
            <form action="${pageContext.request.contextPath}/customer/home" method="POST">
                <div class="modal-body">
                    <!-- FIX: Added missing hidden fields and amount input -->
                    <input type="hidden" name="action" value="performDebit">
                    <input type="hidden" name="userAccountNumber" value="${dashboard.customer.accountNumber}">
                    <div class="mb-3">
                        <label for="debitAmount" class="form-label">Amount</label>
                        <input type="number" class="form-control" id="debitAmount" name="amount" step="0.01" min="0.01" required>
                    </div>
                    <!-- Password field is correct -->
                    <div class="mb-3">
                        <label for="debitPassword" class="form-label">Confirm with Password</label>
                        <input type="password" class="form-control" id="debitPassword" name="password" required>
                    </div>
                </div>
                <div class="modal-footer"><button type="submit" class="btn btn-primary">Withdraw</button></div>
            </form>
        </div>
    </div>
</div>
<!-- Transfer Modal -->
<div class="modal fade" id="transferModal" tabindex="-1">
<div class="modal-dialog">
    <div class="modal-content">
        <div class="modal-header"><h5 class="modal-title">Transfer Funds</h5><button type="button" class="btn-close" data-bs-dismiss="modal"></button></div>
        <form id="transferForm" action="${pageContext.request.contextPath}/customer/home" method="POST">
            <div class="modal-body">
                <input type="hidden" name="action" value="performTransfer">
                <input type="hidden" name="userAccountNumber" value="${dashboard.customer.accountNumber}">
                <div class="mb-3">
                    <label for="receiverAccount" class="form-label">Receiver's Account Number</label>
                    <input type="number" class="form-control" id="receiverAccount" name="receiverAccount" required>
                </div>
                <!-- FIX: ADDED MISSING AMOUNT AND DETAILS FIELDS -->
                <div class="mb-3">
                    <label for="transferAmount" class="form-label">Amount</label>
                    <input type="number" class="form-control" id="transferAmount" name="amount" step="0.01" min="0.01" required>
                </div>
                <div class="mb-3">
                    <label for="details" class="form-label">Details (Optional)</label>
                    <input type="text" class="form-control" id="details" name="details" maxlength="50">
                </div>
                <!-- END FIX -->
                <div class="mb-3">
                    <label for="transferPassword" class="form-label">Confirm with Password</label>
                    <input type="password" class="form-control" id="transferPassword" name="password" required>
                </div>
            </div>
            <div class="modal-footer"><button type="submit" id="transferSubmitBtn" class="btn btn-primary" >Send Money</button></div>
        </form>
    </div>
</div>
</div>



<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>

</body>
</html>