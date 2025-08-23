<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Admin Dashboard</title>

    <!-- Bootstrap CSS -->
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
    <!-- Bootstrap Icons -->
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.3/font/bootstrap-icons.min.css">
    <!-- Google Fonts - Poppins -->
    <link href="https://fonts.googleapis.com/css2?family=Poppins:wght@400;500;600;700&display=swap" rel="stylesheet">

    <style>
        body {
            font-family: 'Poppins', sans-serif;
            background-color: #f4f7fa;
        }

        .wrapper {
            display: flex;
            width: 100%;
            min-height: 100vh;
        }

        /* --- Sidebar --- */
        .sidebar {
            width: 260px;
            background-color: #2c3e50; /* Dark blue-gray */
            color: #ecf0f1;
            transition: all 0.3s;
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

        /* --- Main Content --- */
        .main-content {
            flex-grow: 1;
            padding: 30px;
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

        /* Card for table */
        .card {
            border: none;
            border-radius: 12px;
            box-shadow: 0 4px 25px rgba(0, 0, 0, 0.08);
        }

        .card-header {
            background-color: #ffffff;
            border-bottom: 1px solid #f0f0f0;
            padding: 20px 25px;
            font-weight: 600;
            font-size: 1.1rem;
        }

        /* Table Styles */
        .table {
            margin-bottom: 0;
        }

        .table th {
            font-weight: 600;
            color: #6c757d;
        }

        .table td {
            vertical-align: middle;
        }

    </style>
</head>
<body>

<div class="wrapper">
    <!-- Sidebar -->
    <nav class="sidebar">
        <div class="sidebar-header">
            <h3>Aurionpro Bank</h3>
        </div>

        <ul class="sidebar-nav">
            <li>
                <%-- The 'active' class highlights the current page --%>
                <a href="${pageContext.request.contextPath}/adminDashboard" class="active">
                    <i class="bi bi-grid-1x2-fill"></i> Dashboard
                </a>
            </li>
            <li>
                <a href="${pageContext.request.contextPath}/admin?action=showAddCustomer">
                    <i class="bi bi-person-plus-fill"></i> Add Customer
                </a>
            </li>
            <li>
                <a href="${pageContext.request.contextPath}/admin?action=showPending">
                    <i class="bi bi-bell-fill"></i> Notifications
                    <%-- This can be made dynamic later to show a count --%>
                    <span class="badge rounded-pill bg-danger">3</span>
                </a>
            </li>
            <li>
                <a href="${pageContext.request.contextPath}/logout">
                    <i class="bi bi-box-arrow-left"></i> Logout
                </a>
            </li>
        </ul>
    </nav>

    <!-- Main Page Content -->
    <main class="main-content">
        <header class="main-header">
            <h1>Dashboard</h1>
            <div class="admin-profile">
                <i class="bi bi-person-circle"></i>
                <%-- Assumes the 'user' object with a 'username' property is in the session --%>
                Welcome, <c:out value="${sessionScope.user.username}"/>
            </div>
        </header>

        <div class="card">
            <div class="card-header">
                Customer Account Management
            </div>
            <div class="card-body p-0">
                <div class="table-responsive">
                    <table class="table table-hover">
                        <thead>
                        <tr>
                            <th>Name</th>
                            <th>Balance</th>
                            <th>Status</th>
                            <th class="text-center">Actions</th>
                        </tr>
                        </thead>
                        <tbody>
                        <%--
                            The servlet should set a request attribute "customerList"
                            containing a List of User objects.
                        --%>
                        <c:forEach var="customer" items="${customerList}">
                            <tr>
                                <td><c:out value="${customer.username}"/></td>

                                    <%-- The balance is stored in paise/cents, so divide by 100 for display --%>
                                <td>₹<c:out value="${customer.balance / 100.0}"/></td>

                                <td>
                                    <c:choose>
                                        <c:when test="${customer.deleted}">
                                            <span class="badge bg-danger">Inactive</span>
                                        </c:when>
                                        <c:otherwise>
                                            <span class="badge bg-success">Active</span>
                                        </c:otherwise>
                                    </c:choose>
                                </td>

                                <td class="text-center">
                                        <%-- Edit button links to the servlet with an action and the user_id --%>
                                    <a href="${pageContext.request.contextPath}/admin?action=showEdit&id=${customer.userId}"
                                       class="btn btn-sm btn-outline-primary me-2">
                                        <i class="bi bi-pencil-square"></i> Edit
                                    </a>

                                        <%-- Delete button links to the servlet but has a JS confirmation --%>
                                    <a href="${pageContext.request.contextPath}/admin?action=delete&id=${customer.userId}"
                                       class="btn btn-sm btn-outline-danger"
                                       onclick="return confirm('Are you sure you want to deactivate this user account? This action cannot be undone.')">
                                        <i class="bi bi-trash"></i> Delete
                                    </a>
                                </td>
                            </tr>
                        </c:forEach>
                        </tbody>
                    </table>
                </div>
            </div>
        </div>

    </main>
</div>

<!-- Bootstrap JS Bundle -->
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>