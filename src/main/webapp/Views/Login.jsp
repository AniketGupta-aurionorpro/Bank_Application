<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8" />
    <meta name="viewport" content="width=device-width, initial-scale=1.0" />
    <title>Login - MVC Bank</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">

    <%-- Custom styles to achieve a ShadCN-like aesthetic --%>
    <style>
        @import url('https://fonts.googleapis.com/css2?family=Inter:wght@400;500;600;700&display=swap');

        body {
            font-family: 'Inter', sans-serif;
            background-color: #f8f9fa; /* A light gray background */
        }

        .login-container {
            min-height: 100vh;
            display: flex;
            align-items: center;
            justify-content: center;
            padding: 1rem;
        }

        .login-card {
            width: 100%;
            max-width: 400px; /* Controls the width of the login form */
            background-color: #ffffff;
            border: 1px solid #e2e8f0;
            border-radius: 0.75rem; /* More rounded corners */
            box-shadow: 0 4px 6px -1px rgb(0 0 0 / 0.1), 0 2px 4px -2px rgb(0 0 0 / 0.1);
        }

        .login-card-header {
            padding: 1.5rem;
            border-bottom: 1px solid #e2e8f0;
        }

        .login-card-header h2 {
            font-size: 1.5rem;
            font-weight: 600;
            margin: 0;
            color: #1a202c;
        }

        .login-card-header p {
            margin: 0.25rem 0 0;
            color: #718096;
            font-size: 0.9rem;
        }

        .login-card-body {
            padding: 1.5rem;
        }

        .form-label {
            font-weight: 500;
            color: #4a5568;
            margin-bottom: 0.5rem;
        }

        .form-control {
            border: 1px solid #cbd5e0;
            border-radius: 0.5rem;
            padding: 0.75rem;
            transition: border-color .15s ease-in-out, box-shadow .15s ease-in-out;
        }

        .form-control:focus {
            border-color: #3182ce;
            box-shadow: 0 0 0 2px rgba(49, 130, 206, 0.2);
        }

        .form-control.is-invalid {
            border-color: #e53e3e;
        }

        .btn-primary {
            background-color: #2d3748;
            border-color: #2d3748;
            padding: 0.75rem;
            font-weight: 600;
            border-radius: 0.5rem;
            width: 100%;
        }

        .btn-primary:hover {
            background-color: #1a202c;
            border-color: #1a202c;
        }

        .alert-danger {
            border-radius: 0.5rem;
        }

    </style>
</head>
<body>

<div class="login-container">
    <div class="login-card">
        <div class="login-card-header text-center">
            <h2>Welcome Back</h2>
            <p>Enter your credentials to access your account.</p>
        </div>
        <div class="login-card-body">

            <%-- Global error message for failed login attempts --%>
            <c:if test="${not empty GLOBAL_ERROR}">
                <div class="alert alert-danger" role="alert">
                        ${GLOBAL_ERROR}
                </div>
            </c:if>

            <%-- The action should point to your Login Servlet --%>
            <form method="post" action="${pageContext.request.contextPath}/login" novalidate>

                <div class="mb-3">
                    <label for="username" class="form-label">Username</label>
                    <input type="text" id="username" name="username"
                           class="form-control ${not empty ERRORS.username ? 'is-invalid' : ''}"
                           required />
                    <c:if test="${not empty ERRORS.username}">
                        <div class="invalid-feedback">${ERRORS.username}</div>
                    </c:if>
                </div>

                <div class="mb-4">
                    <label for="password" class="form-label">Password</label>
                    <input type="password" id="password" name="password"
                           class="form-control ${not empty ERRORS.password ? 'is-invalid' : ''}"
                           required />
                    <c:if test="${not empty ERRORS.password}">
                        <div class="invalid-feedback">${ERRORS.password}</div>
                    </c:if>
                </div>

                <button class="btn btn-primary" type="submit">Sign In</button>
            </form>
        </div>
    </div>
</div>

<script>
    // Automatically focus the first field with an error, or the username field by default.
    (function () {
        const hasUsernameError = ${not empty ERRORS.username};
        const hasPasswordError = ${not empty ERRORS.password};

        if (hasUsernameError) {
            document.getElementById('username').focus();
        } else if (hasPasswordError) {
            document.getElementById('password').focus();
        } else {
            // If no errors, focus username field for better UX
            document.getElementById('username').focus();
        }
    })();
</script>
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>