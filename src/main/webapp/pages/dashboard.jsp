<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Dashboard | SecureBank Pro</title>

    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.10.0/font/bootstrap-icons.css">
    <link rel="stylesheet" href="../css/dashboard.css">
    <link rel="stylesheet" href="../css/main.css">

    <script src="../js/dashboard.js" defer></script>

</head>
<body>
<%
    response.setHeader("Cache-Control","no-cache,no-store,must,revalidate"); //HTTP 1.1
    response.setHeader("Pragma","no-cache"); //HTTP 1.0
    response.setHeader("Expires","0");
    if(session.getAttribute("username")==null){
        response.sendRedirect("login.jsp");
    }
%>

<!-- Sidebar -->
<div class="sidebar">
    <div class="sidebar-brand">
        <i class="bi bi-building"></i>
        <span>SecureBank Pro</span>
    </div>
    <nav class="nav flex-column">
        <a href="dashboard.jsp" class="nav-link active">
            <i class="bi bi-speedometer2"></i>
            Dashboard
        </a>
        <a href="customers.jsp" class="nav-link">
            <i class="bi bi-people-fill"></i>
            Customers
        </a>
        <a href="accounts.jsp" class="nav-link">
            <i class="bi bi-credit-card"></i>
            Accounts
        </a>
        <a href="transactions.jsp" class="nav-link">
            <i class="bi bi-arrow-left-right"></i>
            Transactions
        </a>
    </nav>
</div>

<!-- Main Content -->
<div class="main-content">
    <!-- Top Bar -->
    <div class="topbar fade-in">
        <button class="btn btn-outline-primary d-lg-none" id="sidebarToggle">
            <i class="bi bi-list"></i>
        </button>
        <div class="user-profile">
            <img src="https://ui-avatars.com/api/?name=Admin&background=2a4b7c&color=fff" alt="Admin">
            <div>
                <div class="fw-bold">Admin</div>
                <small class="text-muted">Administrator</small>
            </div>
            <a href="${pageContext.request.contextPath}/logout" class="btn btn-outline-danger ms-3">
                <i class="bi bi-box-arrow-right"></i>
            </a>
        </div>
    </div>

    <!-- Page Content -->
    <div class="container-fluid">
        <!-- Page Header -->
        <div class="d-flex justify-content-between align-items-center mb-4">
            <h2 class="fw-bold"><i class="bi bi-speedometer2 text-primary me-2"></i> Dashboard Overview</h2>
            <div class="d-flex">
                <div class="dropdown me-2">
                    <button class="btn btn-outline-secondary dropdown-toggle" type="button" id="timeRangeDropdown" data-bs-toggle="dropdown" aria-expanded="false">
                        <i class="bi bi-calendar-week me-1"></i> Last 30 Days
                    </button>
                    <ul class="dropdown-menu" aria-labelledby="timeRangeDropdown">
                        <li><a class="dropdown-item" href="#">Today</a></li>
                        <li><a class="dropdown-item" href="#">Last 7 Days</a></li>
                        <li><a class="dropdown-item" href="#">Last 30 Days</a></li>
                        <li><a class="dropdown-item" href="#">This Month</a></li>
                        <li><a class="dropdown-item" href="#">This Year</a></li>
                    </ul>
                </div>
                <button class="btn btn-primary">
                    <i class="bi bi-download me-1"></i> Export Report
                </button>
            </div>
        </div>

        <!-- Stats Cards -->
        <div class="stats-grid fade-in">
            <div class="stat-card">
                <div class="stat-number">156</div>
                <div class="stat-label">Total Customers</div>
            </div>
            <div class="stat-card">
                <div class="stat-number">89</div>
                <div class="stat-label">Active Accounts</div>
            </div>
            <div class="stat-card">
                <div class="stat-number">$2.4M</div>
                <div class="stat-label">Total Deposits</div>
            </div>
            <div class="stat-card">
                <div class="stat-number">1,234</div>
                <div class="stat-label">Transactions Today</div>
            </div>
        </div>

        <!-- Recent Activity Card -->
        <div class="card fade-in">
            <div class="card-header">
                <h3 class="card-title"><i class="bi bi-activity"></i> Recent Transactions</h3>
                <div class="search-bar">
                    <i class="bi bi-search"></i>
                    <input type="text" class="form-control" placeholder="Search transactions..." id="transactionSearch">
                </div>
            </div>
            <div class="card-body p-0">
                <div class="table-responsive">
                    <table class="table table-hover mb-0">
                        <thead>
                        <tr>
                            <th>Date</th>
                            <th>Account Number</th>
                            <th>Transaction Type</th>
                            <th>Amount</th>
                            <th>Balance After</th>
                        </tr>
                        </thead>
                        <tbody>
                        <tr>
                            <td>2025-06-16</td>
                            <td>SAV001234567</td>
                            <td class="transaction-deposit">Deposit</td>
                            <td>$1,500.00</td>
                            <td>$15,250.00</td>
                        </tr>
                        <tr>
                            <td>2025-06-15</td>
                            <td>SAV001234568</td>
                            <td class="transaction-withdrawal">Withdrawal</td>
                            <td>$300.00</td>
                            <td>$8,750.00</td>
                        </tr>
                        <tr>
                            <td>2025-06-15</td>
                            <td>SAV001234569</td>
                            <td class="transaction-deposit">Deposit</td>
                            <td>$750.00</td>
                            <td>$12,500.00</td>
                        </tr>
                        <tr>
                            <td>2025-06-14</td>
                            <td>SAV001234567</td>
                            <td class="transaction-withdrawal">Withdrawal</td>
                            <td>$500.00</td>
                            <td>$13,750.00</td>
                        </tr>
                        <tr>
                            <td>2025-06-14</td>
                            <td>SAV001234568</td>
                            <td class="transaction-deposit">Deposit</td>
                            <td>$2,000.00</td>
                            <td>$9,050.00</td>
                        </tr>
                        <tr>
                            <td>2025-06-13</td>
                            <td>SAV001234569</td>
                            <td class="transaction-withdrawal">Withdrawal</td>
                            <td>$1,250.00</td>
                            <td>$11,750.00</td>
                        </tr>
                        <tr>
                            <td>2025-06-10</td>
                            <td>SAV001234567</td>
                            <td class="transaction-deposit">Deposit</td>
                            <td>$5,000.00</td>
                            <td>$14,250.00</td>
                        </tr>
                        </tbody>
                    </table>
                </div>
            </div>
            <div class="card-footer bg-white">
                <nav>
                    <ul class="pagination justify-content-center mb-0">
                        <li class="page-item disabled">
                            <a class="page-link" href="#" tabindex="-1">Previous</a>
                        </li>
                        <li class="page-item active"><a class="page-link" href="#">1</a></li>
                        <li class="page-item"><a class="page-link" href="#">2</a></li>
                        <li class="page-item"><a class="page-link" href="#">3</a></li>
                        <li class="page-item">
                            <a class="page-link" href="#">Next</a>
                        </li>
                    </ul>
                </nav>
            </div>
        </div>
    </div>
</div>

<!-- Bootstrap Bundle with Popper -->
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>

<script>
    document.addEventListener('DOMContentLoaded', function () {
        // Initialize Bootstrap tooltips
        var tooltipTriggerList = [].slice.call(document.querySelectorAll('[data-bs-toggle="tooltip"]'));
        var tooltipList = tooltipTriggerList.map(function (tooltipTriggerEl) {
            return new bootstrap.Tooltip(tooltipTriggerEl);
        });

        // Sidebar toggle for mobile
        document.getElementById('sidebarToggle').addEventListener('click', function() {
            document.querySelector('.sidebar').classList.toggle('active');
        });

        // Simple transaction search functionality
        document.getElementById('transactionSearch').addEventListener('input', function(e) {
            const searchTerm = e.target.value.toLowerCase();
            const rows = document.querySelectorAll('.table tbody tr');

            rows.forEach(row => {
                const text = row.textContent.toLowerCase();
                row.style.display = text.includes(searchTerm) ? '' : 'none';
            });
        });
    });
</script>
</body>
</html>