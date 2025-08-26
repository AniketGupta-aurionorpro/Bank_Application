<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Add New Customer</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.3/font/bootstrap-icons.min.css">
    <link href="https://fonts.googleapis.com/css2?family=Poppins:wght@400;500;600;700&display=swap" rel="stylesheet">

    <style>
        body{font-family:'Poppins',sans-serif;background-color:#f4f7fa}.wrapper{display:flex;width:100%;min-height:100vh}.sidebar{width:260px;min-width:260px;background-color:#2c3e50;color:#ecf0f1;transition:margin-left .3s ease-in-out}.sidebar.collapsed{margin-left:-260px}.sidebar-header{padding:20px;text-align:center;border-bottom:1px solid #34495e}.sidebar-header h3{margin:0;font-weight:600}.sidebar-nav{padding:0;list-style:none}.sidebar-nav li{padding:0}.sidebar-nav a{display:flex;align-items:center;padding:15px 20px;color:#ecf0f1;text-decoration:none;transition:background-color .2s,color .2s;font-weight:500}.sidebar-nav a:hover{background-color:#34495e}.sidebar-nav a.active{background-color:#e74c3c;color:#fff}.sidebar-nav a i{margin-right:15px;font-size:1.2rem;width:20px;text-align:center}.main-content{flex-grow:1;padding:30px;transition:all .3s ease-in-out;overflow-x:hidden}.main-header{display:flex;justify-content:space-between;align-items:center;margin-bottom:30px}.main-header h1{margin:0;font-weight:700}.header-left{display:flex;align-items:center;gap:15px}.card{border:none;border-radius:12px;box-shadow:0 4px 25px rgba(0,0,0,.08)}.card-header{background-color:#fff;border-bottom:1px solid #f0f0f0;padding:20px 25px;font-weight:600;font-size:1.1rem}
    </style>
</head>
<body>

<div class="wrapper">
    <nav id="sidebar" class="sidebar">
        <div class="sidebar-header"><h3>Aurionpro Bank</h3></div>
        <ul class="sidebar-nav">
            <li><a href="${pageContext.request.contextPath}/admin/dashboard"><i class="bi bi-grid-1x2-fill"></i> Dashboard</a></li>
            <li><a href="${pageContext.request.contextPath}/addCustomer" class="active"><i class="bi bi-person-plus-fill"></i> Add Customer</a></li>
            <li><a href="${pageContext.request.contextPath}/admin?action=viewAllTransactions"><i class="bi bi-receipt-cutoff"></i> View All Transactions</a></li>
            <li><a href="${pageContext.request.contextPath}/logoutServlet"><i class="bi bi-box-arrow-left"></i> Logout</a></li>
        </ul>
    </nav>

    <main class="main-content">
        <header class="main-header">
            <div class="header-left"><h1>Add New Customer</h1></div>
        </header>

        <div class="card">
            <div class="card-header">Customer Registration Form</div>
            <div class="card-body p-4">
                <form method="post" action="${pageContext.request.contextPath}/addCustomer" novalidate>
                    <div class="mb-3">
                        <label for="username" class="form-label">Username</label>
                        <input type="text" id="username" name="username" class="form-control ${not empty ERRORS.username ? 'is-invalid' : ''}" value="<c:out value='${FORM_DATA.username}'/>" required>
                        <c:if test="${not empty ERRORS.username}"><div class="invalid-feedback">${ERRORS.username}</div></c:if>
                    </div>
                    <div class="mb-3">
                        <label for="password" class="form-label">Password</label>
                        <input type="password" id="password" name="password" class="form-control ${not empty ERRORS.password ? 'is-invalid' : ''}" required>
                        <c:if test="${not empty ERRORS.password}"><div class="invalid-feedback">${ERRORS.password}</div></c:if>
                    </div>
                    <div class="mb-3">
                        <label for="email" class="form-label">Email Address</label>
                        <input type="email" id="email" name="email" class="form-control ${not empty ERRORS.email ? 'is-invalid' : ''}" value="<c:out value='${FORM_DATA.email}'/>" required>
                        <c:if test="${not empty ERRORS.email}"><div class="invalid-feedback">${ERRORS.email}</div></c:if>
                    </div>
                    <div class="mb-3">
                        <label for="phone" class="form-label">Phone Number</label>
                        <input type="tel" id="phone" name="phone" class="form-control ${not empty ERRORS.phone ? 'is-invalid' : ''}" value="<c:out value='${FORM_DATA.phone}'/>" required>
                        <c:if test="${not empty ERRORS.phone}"><div class="invalid-feedback">${ERRORS.phone}</div></c:if>
                    </div>

                    <!-- NEW: Date of Birth Field -->
                    <div class="mb-3">
                        <label for="dob" class="form-label">Date of Birth</label>
                        <input type="date" id="dob" name="dob"
                               class="form-control ${not empty ERRORS.dob ? 'is-invalid' : ''}"
<%--                               value="<c:out value='${FORM_DATA.dob}'/>" required>--%>
                        <c:if test="${not empty ERRORS.dob}">
                            <div class="invalid-feedback">${ERRORS.dob}</div>
                        </c:if>
                    </div>

                    <div class="d-flex gap-2 mt-4">
                        <button class="btn btn-primary" type="submit">Create Customer Account</button>
                        <a class="btn btn-outline-secondary" href="${pageContext.request.contextPath}/admin/dashboard">Cancel</a>
                    </div>
                </form>
            </div>
        </div>
    </main>
</div>

<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>