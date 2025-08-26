<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Welcome to Aurionpro Bank</title>

    <!-- Bootstrap CSS -->
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">

    <!-- Google Fonts - Poppins is a great choice for a modern look -->
    <link rel="preconnect" href="https://fonts.googleapis.com">
    <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
    <link href="https://fonts.googleapis.com/css2?family=Poppins:wght@400;500;600;700&display=swap" rel="stylesheet">

    <!-- Bootstrap Icons for the features section -->
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.3/font/bootstrap-icons.min.css">

    <style>
        body {
            font-family: 'Poppins', sans-serif;
            background-color: #f8f9fa;
        }

        /* --- Navigation Bar --- */
        .navbar {
            padding: 1rem 0;
            background-color: #ffffff;
            box-shadow: 0 2px 4px rgba(0,0,0,.05);
        }

        .navbar-brand {
            font-weight: 700;
            font-size: 1.5rem;
            color: #0d6efd !important;
        }

        /* --- Hero Section --- */
        .hero-section {
            display: flex;
            align-items: center;
            justify-content: center;
            min-height: 80vh;
            padding: 4rem 0;
            background-color: #ffffff;
        }

        .hero-text h1 {
            font-weight: 700;
            font-size: 3.5rem;
            line-height: 1.2;
            color: #212529;
        }

        .hero-text .lead {
            font-size: 1.25rem;
            color: #6c757d;
            margin: 1.5rem 0;
        }

        .hero-image {
            max-width: 100%;
            height: auto;
        }

        /* --- Buttons --- */
        .btn {
            padding: 0.8rem 2rem;
            border-radius: 50px; /* Fully rounded buttons */
            font-weight: 600;
            font-size: 1rem;
            transition: all 0.3s ease;
        }

        .btn-primary {
            background-color: #0d6efd;
            border: none;
        }

        .btn-primary:hover {
            transform: translateY(-2px);
            box-shadow: 0 4px 15px rgba(13, 110, 253, 0.3);
        }

        .btn-outline-primary {
            border-color: #0d6efd;
            color: #0d6efd;
        }

        .btn-outline-primary:hover {
            background-color: #0d6efd;
            color: #ffffff;
        }

        /* --- Features Section --- */
        .features-section {
            padding: 5rem 0;
        }

        .feature-icon {
            font-size: 3rem;
            color: #0d6efd;
            margin-bottom: 1rem;
        }

        /* --- Footer --- */
        .footer {
            background-color: #212529;
            color: #ffffff;
            padding: 2rem 0;
            margin-top: 4rem;
        }

    </style>
</head>
<body>
<!-- Navigation Bar -->
<nav class="navbar navbar-expand-lg sticky-top">
    <div class="container">
        <a class="navbar-brand" href="#">Aurionpro Bank</a>
        <div class="d-flex ms-auto">
            <!-- Using <a> tags styled as buttons is better for navigation -->
            <a href="${pageContext.request.contextPath}/login" class="btn btn-outline-primary me-2">Login</a>
<%--            <a href="${pageContext.request.contextPath}/add-customer" class="btn btn-primary">Register</a>--%>
        </div>
    </div>
</nav>

<!-- Hero Section -->
<main class="hero-section">
    <div class="container">
        <div class="row align-items-center g-5">
            <div class="col-lg-6 hero-text">
                <h1>Secure, Simple, and Smart Banking.</h1>
                <p class="lead">
                    Manage your finances with confidence and ease. Join us today to experience a new standard of online banking.
                </p>
                <div class="d-flex flex-wrap gap-2">
                    <a href="${pageContext.request.contextPath}/login" class="btn btn-primary">Access Your Account</a>
<%--                    <a href="${pageContext.request.contextPath}/add-customer" class="btn btn-outline-primary">Become a Customer</a>--%>
                </div>
            </div>
            <div class="col-lg-6 text-center">
                <!-- You can replace this placeholder with a nice illustration -->
                <!-- Great illustrations can be found on sites like undraw.co -->
                <img src="${pageContext.request.contextPath}/IMG/undraw_be-the-hero_bce0.svg" alt="Banking Illustration" class="hero-image rounded shadow-sm">
            </div>
        </div>
    </div>
</main>

<!-- Features Section -->
<section class="features-section text-center">
    <div class="container">
        <h2 class="mb-5">Why Choose Us?</h2>
        <div class="row">
            <div class="col-md-4 mb-4">
                <i class="bi bi-shield-check feature-icon"></i>
                <h3>Bank-Level Security</h3>
                <p class="text-muted">Your data and transactions are protected with state-of-the-art security measures.</p>
            </div>
            <div class="col-md-4 mb-4">
                <i class="bi bi-phone feature-icon"></i>
                <h3>Easy Mobile Access</h3>
                <p class="text-muted">Access your accounts anytime, anywhere, from any device with our responsive design.</p>
            </div>
            <div class="col-md-4 mb-4">
                <i class="bi bi-lightning-charge feature-icon"></i>
                <h3>Fast Transactions</h3>
                <p class="text-muted">Transfer funds between accounts instantly and view your updated balance in real-time.</p>
            </div>
        </div>
    </div>
</section>

<!-- Footer -->
<footer class="footer text-center">
    <div class="container">
        <p class="m-0">&copy; 2025 Aurionpro Bank. All Rights Reserved.</p>
    </div>
</footer>

<!-- Bootstrap JS Bundle -->
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>