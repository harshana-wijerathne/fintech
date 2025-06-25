<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Customer Management | SecureBank Pro</title>


    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.10.0/font/bootstrap-icons.css">
    <link rel="stylesheet" href="../css/customer.css">
    <link rel="stylesheet" href="../css/main.css">

    <script src="../js/customers.js" type="module" defer></script>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/notification.css">
    <script src="${pageContext.request.contextPath}/js/notification.js" defer></script>

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
        <a href="customers.jsp" class="nav-link active">
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
            <a href="${pageContext.request.contextPath}/logout" class="btn btn-outline-danger ms-3">Logout
                <i class="bi bi-box-arrow-right ms-3"></i>
            </a>
        </div>
    </div>

    <!-- Page Content -->
    <div class="container-fluid">
        <!-- Page Header -->
        <div class="d-flex justify-content-between align-items-center mb-4">
            <h2 class="fw-bold"><i class="bi bi-people-fill text-primary me-2"></i> Customer Management</h2>
            <button class="btn btn-primary" onclick="AddNewCustomer()">
                <i class="bi bi-plus-lg me-1"></i> Add New Customer
            </button>
        </div>

        <!-- Customer List Card -->
        <div class="card fade-in">
            <div class="card-header">
                <h3 class="card-title"><i class="bi bi-list-ul"></i> Customer List</h3>
                <div class="search-bar">
                    <i class="bi bi-search"></i>
                    <input type="text" class="form-control" placeholder="Search by name or NIC..."
                           id="customerSearch" onkeyup="searchUser(event)">
                </div>
            </div>
            <div class="card-body p-0">
                <div class="table-responsive">
                    <table class="table table-hover mb-0">
                        <thead>
                        <tr>
                            <th>Full Name</th>
                            <th>NIC/Passport</th>
                            <th>Mobile</th>
                            <th>Email</th>
                            <th class="text-end">Actions</th>
                        </tr>
                        </thead>
                        <tbody id="customersTableBody">
                        <!-- Customers will be loaded here dynamically -->
                        <tr>
                            <td colspan="5" class="text-center py-4">
                                <div class="spinner-border text-primary" role="status">
                                    <span class="visually-hidden">Loading...</span>
                                </div>
                            </td>
                        </tr>
                        </tbody>
                    </table>
                </div>
            </div>
            <div class="card-footer bg-white">
                <nav>
                    <ul class="pagination justify-content-center align-items-center mb-0 gap-2">
                        <li id="nextPage" class="page-item disabled" onclick="prevPage()">
                            <a class="page-link" tabindex="-1">Previous</a>
                        </li>
                        <li id="page-item1" class="page-item active"><a class="page-link" id="page-link" href="#">Page 1</a></li>
<%--                        <li id="page-item1" class="page-item"><a class="page-link" href="#">2</a></li>--%>
<%--                        <li id="page-item1" class="page-item"><a class="page-link" href="#">3</a></li>--%>
                        <li id="prevPage" class="page-item" onclick="nextPage()">
                            <a class="page-link" href="#">Next</a>
                        </li>
                    </ul>
                </nav>
            </div>
        </div>
    </div>
</div>


<%-----------------------------------------Modals-----------------------------------%>

<!-- Add New Customer Modal -->
<div class="modal fade" id="AddNewCustomerModal" tabindex="-1" aria-hidden="true">
    <div class="modal-dialog modal-lg">
        <div class="modal-content">
            <div class="modal-header bg-primary text-white">
                <h5 class="modal-title"><i class="bi bi-person-plus-fill me-2"></i> Register New Customer</h5>
                <button type="button" class="btn-close btn-close-white" onclick="closeModal()"></button>
            </div>
            <form id="customerForm" class="needs-validation" novalidate>
                <div class="modal-body">
                    <div class="row g-3">
                        <!-- Personal Info Section -->
                        <div class="col-12">
                            <h6 class="border-bottom pb-2 mb-3 text-primary">
                                <i class="bi bi-person-lines-fill me-2"></i>Personal Information
                            </h6>
                        </div>

                        <div class="col-md-6">
                            <label for="fullName" class="form-label">Full Name <span class="text-danger">*</span></label>
                            <div class="input-group">
                                <span class="input-group-text"><i class="bi bi-person"></i></span>
                                <input type="text" class="form-control" id="fullName" name="fullName" required>
                                <div class="invalid-feedback">Please provide a valid name</div>
                            </div>
                        </div>

                        <div class="col-md-6">
                            <label for="nicPassport" class="form-label">NIC/Passport No <span class="text-danger">*</span></label>
                            <div class="input-group">
                                <span class="input-group-text"><i class="bi bi-credit-card"></i></span>
                                <input type="text" class="form-control" id="nicPassport" name="nicPassport" required>
                                <div class="invalid-feedback">Please provide a valid ID</div>
                            </div>
                        </div>

                        <div class="col-md-6">
                            <label for="dob" class="form-label">Date of Birth <span class="text-danger">*</span></label>
                            <div class="input-group">
                                <span class="input-group-text"><i class="bi bi-calendar"></i></span>
                                <input type="date" class="form-control" id="dob" name="dob" required>
                                <div class="invalid-feedback">Please select a valid date</div>
                            </div>
                        </div>

                        <!-- Contact Info Section -->
                        <div class="col-12 mt-4">
                            <h6 class="border-bottom pb-2 mb-3 text-primary">
                                <i class="bi bi-telephone-fill me-2"></i>Contact Information
                            </h6>
                        </div>

                        <div class="col-md-6">
                            <label for="mobile" class="form-label">Mobile Number <span class="text-danger">*</span></label>
                            <div class="input-group">
                                <span class="input-group-text"><i class="bi bi-phone"></i></span>
                                <input type="tel" class="form-control" id="mobile" name="mobile" required>
                                <div class="invalid-feedback">Please provide a valid number</div>
                            </div>
                        </div>

                        <div class="col-md-6">
                            <label for="email" class="form-label">Email Address</label>
                            <div class="input-group">
                                <span class="input-group-text"><i class="bi bi-envelope"></i></span>
                                <input type="email" class="form-control" id="email" name="email">
                                <div class="invalid-feedback">Please provide a valid email</div>
                            </div>
                        </div>

                        <div class="col-12">
                            <label for="address" class="form-label">Address <span class="text-danger">*</span></label>
                            <div class="input-group">
                                <span class="input-group-text"><i class="bi bi-geo-alt"></i></span>
                                <input type="text" class="form-control" id="address" name="address" required>
                                <div class="invalid-feedback">Please provide a valid address</div>
                            </div>
                        </div>

                        <!-- Additional Info Section -->
                        <div class="col-12 mt-4">
                            <h6 class="border-bottom pb-2 mb-3 text-primary">
                                <i class="bi bi-info-circle-fill me-2"></i>Additional Information
                            </h6>
                        </div>

                        <div class="col-md-6">
                            <label for="customerType" class="form-label">Customer Type</label>
                            <select class="form-select" id="customerType" name="customerType">
                                <option value="INDIVIDUAL">Individual</option>
                                <option value="BUSINESS">Business</option>
                                <option value="VIP">VIP</option>
                            </select>
                        </div>

                        <div class="col-md-6">
                            <label for="customerSince" class="form-label">Customer Since</label>
                            <input type="date" class="form-control" id="customerSince" name="customerSince" value="">
                        </div>
                    </div>
                </div>
                <div class="modal-footer bg-light">
                    <button type="button" class="btn btn-outline-secondary" onclick="closeModal()">
                        <i class="bi bi-x-circle me-1"></i> Cancel
                    </button>
                    <button type="button" class="btn btn-outline-info" onclick="dummyData()">
                        <i class="bi bi-magic me-1"></i> Demo Data
                    </button>
                    <button type="submit" class="btn btn-primary">
                        <i class="bi bi-save-fill me-1"></i> Register Customer
                    </button>
                </div>
            </form>
        </div>
    </div>
</div>

<%-- Update Customer Modal--%>
<div class="modal fade" id="UpdateCustomerModal" tabindex="-1" aria-hidden="true">
    <div class="modal-dialog modal-lg">
        <div class="modal-content">
            <div class="modal-header bg-warning text-dark">
                <h5 class="modal-title"><i class="bi bi-person-gear me-2"></i> Update Customer</h5>
                <button type="button" class="btn-close" onclick="closeModalUpdate()"></button>
            </div>
            <form id="updateCustomerForm" class="needs-validation" novalidate>
                <div class="modal-body">
                    <div class="alert alert-info">
                        <i class="bi bi-info-circle-fill me-2"></i> Updating customer ID: <strong id="updateCustomerId"></strong>
                    </div>

                    <div class="row g-3">
                        <div class="col-md-6">
                            <label for="updateFullName" class="form-label">Full Name</label>
                            <div class="input-group">
                                <span class="input-group-text"><i class="bi bi-person"></i></span>
                                <input type="text" class="form-control" id="updateFullName" name="fullName" required>
                            </div>
                        </div>

                        <div class="col-md-6">
                            <label class="form-label">NIC/Passport No</label>
                            <div class="input-group">
                                <span class="input-group-text"><i class="bi bi-credit-card"></i></span>
                                <input type="text" class="form-control bg-light" id="updateNicPassport" readonly>
                                <span class="input-group-text text-muted small">Cannot be changed</span>
                            </div>
                        </div>

                        <div class="col-md-6">
                            <label for="updateDob" class="form-label">Date of Birth</label>
                            <div class="input-group">
                                <span class="input-group-text"><i class="bi bi-calendar"></i></span>
                                <input type="date" class="form-control" id="updateDob" name="dob">
                            </div>
                        </div>

                        <div class="col-md-6">
                            <label for="updateMobile" class="form-label">Mobile Number</label>
                            <div class="input-group">
                                <span class="input-group-text"><i class="bi bi-phone"></i></span>
                                <input type="tel" class="form-control" id="updateMobile" name="mobile">
                            </div>
                        </div>

                        <div class="col-12">
                            <label for="updateAddress" class="form-label">Address</label>
                            <div class="input-group">
                                <span class="input-group-text"><i class="bi bi-geo-alt"></i></span>
                                <input type="text" class="form-control" id="updateAddress" name="address">
                            </div>
                        </div>

                        <div class="col-md-6">
                            <label for="updateEmail" class="form-label">Email Address</label>
                            <div class="input-group">
                                <span class="input-group-text"><i class="bi bi-envelope"></i></span>
                                <input type="email" class="form-control" id="updateEmail" name="email">
                            </div>
                        </div>

                        <div class="col-md-6">
                            <label for="updateCustomerType" class="form-label">Customer Type</label>
                            <select class="form-select" id="updateCustomerType" name="customerType">
                                <option value="INDIVIDUAL">Individual</option>
                                <option value="BUSINESS">Business</option>
                                <option value="VIP">VIP</option>
                            </select>
                        </div>
                    </div>
                </div>
                <div class="modal-footer bg-light">
                    <button type="button" class="btn btn-outline-danger me-auto" onclick="confirmDelete()">
                        <i class="bi bi-trash-fill me-1"></i> Delete Customer
                    </button>
                    <button type="button" class="btn btn-outline-secondary" onclick="closeModalUpdate()">
                        <i class="bi bi-x-circle me-1"></i> Cancel
                    </button>
                    <button type="submit" class="btn btn-warning text-dark">
                        <i class="bi bi-save-fill me-1"></i> Save Changes
                    </button>
                </div>
            </form>
        </div>
    </div>
</div>

<%-- View Customer Modal--%>
<div class="modal fade" id="ViewCustomerModal" tabindex="-1" aria-hidden="true">
    <div class="modal-dialog modal-lg">
        <div class="modal-content">
            <div class="modal-header bg-info text-white">
                <h5 class="modal-title"><i class="bi bi-person-badge me-2"></i> Customer Details</h5>
                <button type="button" class="btn-close btn-close-white" onclick="closeModalView()"></button>
            </div>
            <div class="modal-body">
                <div class="row">
                    <!-- Customer Profile Column -->
                    <div class="col-md-4 border-end">
                        <div class="text-center mb-4">
                            <div class="avatar-placeholder bg-primary text-white rounded-circle d-flex align-items-center justify-content-center mx-auto"
                                 style="width: 100px; height: 100px; font-size: 2.5rem;">
                                <span id="customerInitial">J</span>
                            </div>
                            <h4 class="mt-3 mb-1" id="viewFullName">John Doe</h4>
                            <div class="badge bg-success" id="viewCustomerType">INDIVIDUAL</div>

                            <hr class="my-3">

                            <div class="text-start">
                                <h6 class="text-muted mb-3"><i class="bi bi-info-circle me-2"></i>Basic Information</h6>
                                <ul class="list-unstyled">
                                    <li class="mb-2">
                                        <i class="bi bi-credit-card me-2 text-muted"></i>
                                        <strong>NIC:</strong> <span id="viewNicPassport">199012345678</span>
                                    </li>
                                    <li class="mb-2">
                                        <i class="bi bi-calendar me-2 text-muted"></i>
                                        <strong>DOB:</strong> <span id="viewDob">15/05/1990</span>
                                    </li>
                                    <li class="mb-2">
                                        <i class="bi bi-clock-history me-2 text-muted"></i>
                                        <strong>Member Since:</strong> <span id="viewCustomerSince">2023-01-15</span>
                                    </li>
                                </ul>
                            </div>
                        </div>
                    </div>

                    <!-- Customer Details Column -->
                    <div class="col-md-8">
                        <h6 class="text-muted mb-3"><i class="bi bi-person-lines-fill me-2"></i>Contact Details</h6>
                        <div class="row mb-4">
                            <div class="col-6 mb-3">
                                <label class="form-label small text-muted">Mobile Number</label>
                                <div class="d-flex align-items-center">
                                    <i class="bi bi-phone me-2 text-primary"></i>
                                    <span class="fw-bold" id="viewMobile">+94771234567</span>
                                </div>
                            </div>
                            <div class="col-6 mb-3">
                                <label class="form-label small text-muted">Email Address</label>
                                <div class="d-flex align-items-center">
                                    <i class="bi bi-envelope me-2 text-primary"></i>
                                    <span class="fw-bold" id="viewEmail">john.doe@example.com</span>
                                </div>
                            </div>
                            <div class="col-12">
                                <label class="form-label small text-muted">Address</label>
                                <div class="d-flex">
                                    <i class="bi bi-geo-alt me-2 text-primary"></i>
                                    <span class="fw-bold" id="viewAddress">123 Main Street, Colombo 05, Sri Lanka</span>
                                </div>
                            </div>
                        </div>

                        <hr class="my-3">

                        <h6 class="text-muted mb-3"><i class="bi bi-bank me-2"></i>Account Summary</h6>
                        <div class="row">
                            <div class="col-md-6 mb-3">
                                <div class="card border-0 bg-light">
                                    <div class="card-body p-3">
                                        <div class="d-flex justify-content-between align-items-center">
                                            <div>
                                                <h6 class="mb-0 text-muted">Total Accounts</h6>
                                                <h3 class="mb-0 text-primary" id="totalAccounts">3</h3>
                                            </div>
                                            <i class="bi bi-credit-card text-primary" style="font-size: 2rem;"></i>
                                        </div>
                                    </div>
                                </div>
                            </div>
                            <div class="col-md-6 mb-3">
                                <div class="card border-0 bg-light">
                                    <div class="card-body p-3">
                                        <div class="d-flex justify-content-between align-items-center">
                                            <div>
                                                <h6 class="mb-0 text-muted">Total Balance</h6>
                                                <h3 class="mb-0 text-success" id="totalBalance">$24,500.00</h3>
                                            </div>
                                            <i class="bi bi-cash-stack text-success" style="font-size: 2rem;"></i>
                                        </div>
                                    </div>
                                </div>
                            </div>
                        </div>

                        <div class="mt-3">
                            <button class="btn btn-sm btn-outline-primary me-2" onclick="viewCustomerAccounts()">
                                <i class="bi bi-credit-card me-1"></i> View Accounts
                            </button>
                            <button class="btn btn-sm btn-outline-secondary me-2" onclick="printCustomerDetails()">
                                <i class="bi bi-printer me-1"></i> Print
                            </button>
                            <button class="btn btn-sm btn-outline-warning" onclick="editCurrentCustomer()">
                                <i class="bi bi-pencil me-1"></i> Edit Profile
                            </button>
                        </div>
                    </div>
                </div>
            </div>
            <div class="modal-footer bg-light">
                <button type="button" class="btn btn-outline-secondary" onclick="closeModalView()">
                    <i class="bi bi-x-circle me-1"></i> Close
                </button>
            </div>
        </div>
    </div>
</div>


<%-- Delete Cusotmer Modal--%>
<div class="modal fade" id="deleteConfirmationModal" tabindex="-1" aria-hidden="true">
    <div class="modal-dialog modal-dialog-centered">
        <div class="modal-content border-0">
            <div class="modal-header bg-danger text-white">
                <h5 class="modal-title">
                    <i class="bi bi-exclamation-triangle-fill me-2"></i>
                    Confirm Deletion
                </h5>
                <button type="button" class="btn-close btn-close-white" data-bs-dismiss="modal" aria-label="Close"></button>
            </div>
            <div class="modal-body">
                <div class="d-flex align-items-center mb-3">
                    <div class="flex-shrink-0">
                        <div class="avatar bg-danger bg-opacity-10 text-danger rounded-circle d-flex align-items-center justify-content-center"
                             style="width: 48px; height: 48px;">
                            <i class="bi bi-trash-fill fs-4"></i>
                        </div>
                    </div>
                    <div class="flex-grow-1 ms-3">
                        <h6 class="mb-1">You're about to delete a customer</h6>
                        <p class="mb-0 text-muted small">This action cannot be undone. All associated accounts will also be removed.</p>
                    </div>
                </div>

                <div class="alert alert-warning mt-3">
                    <div class="d-flex">
                        <i class="bi bi-exclamation-octagon-fill me-2"></i>
                        <div>
                            <strong>Customer ID:</strong> <span id="deleteCustomerId" class="fw-bold">CUST-1001</span>
                            <div class="mt-1"><strong>Name:</strong> <span id="deleteCustomerName">John Doe</span></div>
                        </div>
                    </div>
                </div>

                <div class="form-check mt-3">
                    <input class="form-check-input" type="checkbox" id="confirmDeleteCheck">
                    <label class="form-check-label small text-muted" for="confirmDeleteCheck">
                        I understand this will permanently delete all customer data
                    </label>
                </div>
            </div>
            <div id="delete-modal" class="modal-footer">
                <button type="button" class="btn btn-outline-secondary" data-bs-dismiss="modal">
                    <i class="bi bi-x-circle me-1"></i> Cancel
                </button>
                <div id="delete-modal">
                    <button type="button" class="btn btn-danger" id="confirmDeleteBtn" disabled>
                        <i class="bi bi-trash-fill me-1"></i> Delete Permanently
                    </button>
                </div>

            </div>
        </div>
    </div>
</div>


<!-- Bootstrap Bundle with Popper -->
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>

</body>
</html>
