<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Account Management | SecureBank Pro</title>

    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.10.0/font/bootstrap-icons.css">
    <link rel="stylesheet" href="../css/account.css">
    <link rel="stylesheet" href="../css/main.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/notification.css">
    <script src="${pageContext.request.contextPath}/js/notification.js" defer></script>

</head>
<body>
<%
    response.setHeader("Cache-Control", "no-cache,no-store,must,revalidate"); //HTTP 1.1
    response.setHeader("Pragma", "no-cache"); //HTTP 1.0
    response.setHeader("Expires", "0");

    if (session.getAttribute("username") == null) {
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
        <a href="dashboard.jsp" class="nav-link">
            <i class="bi bi-speedometer2"></i>
            Dashboard
        </a>
        <a href="customers.jsp" class="nav-link">
            <i class="bi bi-people-fill"></i>
            Customers
        </a>
        <a href="accounts.jsp" class="nav-link active">
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
            <h2 class="fw-bold"><i class="bi bi-credit-card text-primary me-2"></i> Account Management</h2>
            <div class="d-flex">
                <div class="dropdown me-2">
                    <button class="btn btn-outline-secondary dropdown-toggle" type="button" id="accountTypeDropdown"
                            data-bs-toggle="dropdown" aria-expanded="false">
                        <i class="bi bi-funnel me-1"></i> Filter Accounts
                    </button>
                    <ul class="dropdown-menu" aria-labelledby="accountTypeDropdown">
                        <li><a class="dropdown-item" href="#">All Accounts</a></li>
                        <li><a class="dropdown-item" href="#">Savings Accounts</a></li>
                        <li><a class="dropdown-item" href="#">Checking Accounts</a></li>
                        <li><a class="dropdown-item" href="#">Business Accounts</a></li>
                    </ul>
                </div>
                <button class="btn btn-primary">
                    <i class="bi bi-download me-1"></i> Export
                </button>
            </div>
        </div>

        <!-- New Account Card -->
        <div class="card fade-in">
            <div class="card-header">
                <h3 class="card-title"><i class="bi bi-plus-circle"></i> Open New Savings Account</h3>
            </div>
            <form class="form-grid" id="accountForm">
                <div class="mb-3">
                    <label for="customerId" class="form-label">Select Customer</label>
                    <select class="form-select" id="account-select" name="customerId" required>
                        <option value="">Choose a customer...</option>
                        <option value="1">John Smith - 199512345678</option>
                        <option value="2">Sarah Johnson - 198712345678</option>
                        <option value="3">Mike Davis - 199012345678</option>
                    </select>
                </div>
                <div class="mb-3">
                    <label for="openingDate" class="form-label">Opening Date</label>
                    <input type="date" class="form-control" id="openingDate" name="openingDate" required>
                </div>
                <div class="mb-3">
                    <label for="customerId" class="form-label">Select Account Tpe</label>
                    <select class="form-select" id="account-select" name="accountType" required>
                        <option value="">Choose...</option>
                        <option value="SAVING">SAVING</option>
                        <option value="PREMIUM">PREMIUM</option>
                        <option value="CHILDREN">CHILDREN</option>
                        <option value="WOMEN">WOMEN</option>
                        <option value="SENIOR_CITIZEN">SENIOR_CITIZEN</option>
                    </select>
                </div>
                <div class="mb-3">
                    <label for="initialDeposit" class="form-label">Initial Deposit</label>
                    <div class="input-group">
                        <span class="input-group-text">$</span>
                        <input type="number" class="form-control" id="initialDeposit" name="initialDeposit" min="100"
                               step="1" required>
                    </div>
                </div>
                <div class="mb-3 d-flex justify-content-end">
                    <button type="submit" class="btn btn-success">
                        <i class="bi bi-plus-circle me-1"></i> Open Account
                    </button>
                </div>
            </form>
        </div>

        <!-- Account List Card -->
        <div class="card fade-in">
            <div class="card-header">
                <h3 class="card-title"><i class="bi bi-credit-card"></i> Account Details</h3>
                <div class="search-bar">
                    <i class="bi bi-search"></i>
                    <input onkeyup="searchUser(event)" type="text" class="form-control" placeholder="Search accounts..." id="accountSearch">
                </div>
            </div>
            <div class="card-body p-0">
                <div class="table-responsive">
                    <table class="table table-hover mb-0">
                        <thead>
                        <tr>
                            <th>Account Number</th>
                            <th>Customer Name</th>
                            <th>Opening Date</th>
                            <th>Current Balance</th>
                            <th class="text-end">Actions</th>
                        </tr>
                        </thead>
                        <tbody id="accountTableBody">
                        <tr>
                            <td>SAV001234567</td>
                            <td>John Smith</td>
                            <td>2024-01-15</td>
                            <td class="currency">$15,250.00</td>
                            <td class="text-end">
                                <button class="btn btn-sm btn-outline-primary me-1"
                                        onclick="viewAccountDetails('SAV001234567')">
                                    <i class="bi bi-eye"></i> View
                                </button>
                                <button class="btn btn-sm btn-outline-secondary">
                                    <i class="bi bi-printer"></i>
                                </button>
                            </td>
                        </tr>
                        <tr>
                            <td>SAV001234568</td>
                            <td>Sarah Johnson</td>
                            <td>2024-02-20</td>
                            <td class="currency">$8,750.00</td>
                            <td class="text-end">
                                <button class="btn btn-sm btn-outline-primary me-1"
                                        onclick="viewAccountDetails('SAV001234568')">
                                    <i class="bi bi-eye"></i> View
                                </button>
                                <button class="btn btn-sm btn-outline-secondary">
                                    <i class="bi bi-printer"></i>
                                </button>
                            </td>
                        </tr>
                        <tr>
                            <td>SAV001234569</td>
                            <td>Mike Davis</td>
                            <td>2024-03-10</td>
                            <td class="currency">$12,500.00</td>
                            <td class="text-end">
                                <button class="btn btn-sm btn-outline-primary me-1"
                                        onclick="viewAccountDetails('SAV001234569')">
                                    <i class="bi bi-eye"></i> View
                                </button>
                                <button class="btn btn-sm btn-outline-secondary">
                                    <i class="bi bi-printer"></i>
                                </button>
                            </td>
                        </tr>
                        </tbody>
                    </table>
                </div>
            </div>
            <div class="card-footer bg-white">
                <nav>
                    <%--                    <ul class="pagination justify-content-center mb-0">--%>
                    <%--                        <li class="page-item disabled">--%>
                    <%--                            <a class="page-link" href="#" tabindex="-1">Previous</a>--%>
                    <%--                        </li>--%>
                    <%--                        <li class="page-item active"><a class="page-link" href="#">1</a></li>--%>
                    <%--                        <li class="page-item"><a class="page-link" href="#">2</a></li>--%>
                    <%--                        <li class="page-item"><a class="page-link" href="#">3</a></li>--%>
                    <%--                        <li class="page-item">--%>
                    <%--                            <a class="page-link" href="#">Next</a>--%>
                    <%--                        </li>--%>
                    <%--                    </ul>--%>
                    <ul class="pagination justify-content-center align-items-center mb-0 gap-2">
                        <li id="nextPage" class="page-item disabled" onclick="prevPage()">
                            <div class="page-link" tabindex="-1">Previous</div>
                        </li>
                        <li id="page-item1" class="page-item active"><a class="page-link" id="page-link" >Page
                            1</a></li>
                        <%--                        <li id="page-item1" class="page-item"><a class="page-link" href="#">2</a></li>--%>
                        <%--                        <li id="page-item1" class="page-item"><a class="page-link" href="#">3</a></li>--%>
                        <li id="prevPage" class="page-item" onclick="nextPage()">
                            <div class="page-link">Next</div>
                        </li>
                    </ul>
                </nav>
            </div>
        </div>
    </div>
</div>


<%--  Modal windows--%>

<!-- Account Details Modal -->
<div class="modal fade" id="accountDetailsModal" tabindex="-1" aria-hidden="true">
    <div class="modal-dialog modal-lg">
        <div class="modal-content">
            <div class="modal-header bg-primary text-white">
                <h5 class="modal-title">
                    <i class="bi bi-person-badge me-2"></i>
                    Customer Account Details
                </h5>
                <button type="button" class="btn-close btn-close-white" data-bs-dismiss="modal"
                        aria-label="Close"></button>
            </div>
            <div class="modal-body">
                <div class="row">
                    <!-- Left Column - Profile -->
                    <div class="col-md-5 border-end">
                        <div class="text-center mb-4">
                            <div class="avatar-placeholder bg-primary text-white rounded-circle d-flex align-items-center justify-content-center mx-auto"
                                 style="width: 100px; height: 100px; font-size: 2.5rem;">
                                <span id="customerInitial">H</span>
                            </div>
                            <h4 class="mt-3 mb-1" id="customerFullName">Harshana</h4>
                            <div class="badge bg-success">Active Account</div>

                            <div class="mt-3">
                                <div class="card border-0 bg-light">
                                    <div class="card-body py-2">
                                        <div class="d-flex justify-content-between align-items-center">
                                            <div>
                                                <h6 class="mb-0 text-muted">Account Number</h6>
                                                <h4 class="mb-0 text-primary" id="accountNumberDisplay">
                                                    70032500000020</h4>
                                            </div>
                                            <i class="bi bi-credit-card text-primary" style="font-size: 1.75rem;"></i>
                                        </div>
                                    </div>
                                </div>
                            </div>
                        </div>

                        <hr>

                        <div class="mb-3">
                            <h6 class="text-muted mb-3">
                                <i class="bi bi-file-person me-2"></i>Basic Information
                            </h6>
                            <ul class="list-unstyled">
                                <li class="mb-2">
                                    <i class="bi bi-credit-card me-2 text-muted"></i>
                                    <strong>NIC/Passport:</strong> <span id="nicPassportDisplay">199025600142</span>
                                </li>
                                <li class="mb-2">
                                    <i class="bi bi-calendar me-2 text-muted"></i>
                                    <strong>Date of Birth:</strong> <span id="dobDisplay">20/06/2000</span>
                                </li>
                                <li class="mb-2">
                                    <i class="bi bi-clock-history me-2 text-muted"></i>
                                    <strong>Member Since:</strong> <span id="openingDateDisplay">12/06/2025</span>
                                </li>
                            </ul>
                        </div>
                    </div>

                    <!-- Right Column - Account Details -->
                    <div class="col-md-7">
                        <h6 class="text-muted mb-3">
                            <i class="bi bi-geo-alt me-2"></i>Contact Information
                        </h6>
                        <div class="row mb-4">
                            <div class="col-12 mb-3">
                                <label class="form-label small text-muted">Address</label>
                                <div class="d-flex">
                                    <i class="bi bi-house me-2 text-primary"></i>
                                    <span class="fw-bold" id="addressDisplay">123 Main Street, Colombo</span>
                                </div>
                            </div>
                            <div class="col-md-6 mb-3">
                                <label class="form-label small text-muted">Email Address</label>
                                <div class="d-flex align-items-center">
                                    <i class="bi bi-envelope me-2 text-primary"></i>
                                    <span class="fw-bold" id="emailDisplay">john.doe@example.com</span>
                                </div>
                            </div>
                        </div>

                        <hr class="my-3">

                        <h6 class="text-muted mb-3">
                            <i class="bi bi-bank me-2"></i>Account Status
                        </h6>
                        <div class="row">
                            <div class="col-md-6 mb-3">
                                <div class="card border-0 bg-light">
                                    <div class="card-body p-3">
                                        <div class="d-flex justify-content-between align-items-center">
                                            <div>
                                                <h6 class="mb-0 text-muted">Current Balance</h6>
                                                <h3 class="mb-0 text-success" id="balanceDisplay">LKR 105.00</h3>
                                            </div>
                                            <i class="bi bi-cash-stack text-success" style="font-size: 1.75rem;"></i>
                                        </div>
                                    </div>
                                </div>
                            </div>
                            <div class="col-md-6 mb-3">
                                <div class="card border-0 bg-light">
                                    <div class="card-body p-3">
                                        <div class="d-flex justify-content-between align-items-center">
                                            <div>
                                                <h6 class="mb-0 text-muted">Account Type</h6>
                                                <h4 class="mb-0 text-primary">Savings</h4>
                                            </div>
                                            <i class="bi bi-piggy-bank text-primary" style="font-size: 1.75rem;"></i>
                                        </div>
                                    </div>
                                </div>
                            </div>
                        </div>

                        <div class="mt-3">
                            <button class="btn btn-sm btn-outline-primary me-2">
                                <i class="bi bi-printer me-1"></i> Print Statement
                            </button>
                            <button class="btn btn-sm btn-outline-warning me-2">
                                <i class="bi bi-pencil me-1"></i> Edit Details
                            </button>
                            <button class="btn btn-sm btn-outline-danger">
                                <i class="bi bi-trash me-1"></i> Close Account
                            </button>
                        </div>
                    </div>
                </div>
            </div>
            <div class="modal-footer">
                <button type="button" class="btn btn-outline-secondary" data-bs-dismiss="modal">
                    <i class="bi bi-x-circle me-1"></i> Close
                </button>
            </div>
        </div>
    </div>
</div>


<!-- Bootstrap Bundle with Popper -->
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
<script src="../js/account.js" defer></script>

</body>
</html>