<%--
  Created by IntelliJ IDEA.
  User: harshana
  Date: 6/17/25
  Time: 1:02 AM
  To change this template use File | Settings | File Templates.
--%>
<%--<%@ page contentType="text/html;charset=UTF-8" language="java" %>--%>
<html>
<head>
    <title>Title</title>
    <link rel="stylesheet" href="css/header.css">
    <script type="module" src="https://unpkg.com/lucide@latest/dist/umd/lucide.js"></script>
    <script>
        document.addEventListener('DOMContentLoaded', function() {
            lucide.createIcons();
        });
    </script>
</head>
<body>
<header class="header">
    <div class="logo">
        <i data-lucide="building-2"></i>
        SecureBank Pro
    </div>
    <nav>
        <ul class="nav-menu">
            <li class="nav-item active" data-page="dashboard">
                <i data-lucide="layout-dashboard"></i>
                Dashboard
            </li>
            <li class="nav-item" data-page="customers">
                <i data-lucide="users"></i>
                Customers
            </li>
            <li class="nav-item" data-page="accounts">
                <i data-lucide="credit-card"></i>
                Accounts
            </li>
            <li class="nav-item" data-page="transactions">
                <i data-lucide="arrow-left-right"></i>
                Transactions
            </li>
        </ul>
    </nav>
    <div class="user-info">
        <span>Welcome, Admin</span>
        <button class="logout-btn" onclick="logout()">
            <i data-lucide="log-out"></i>
            Logout
        </button>
    </div>
</header>
</body>
</html>
