<%--
  Created by IntelliJ IDEA.
  User: harshana
  Date: 6/23/25
  Time: 9:28 PM
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
  <head>
    <title>Title</title>


      <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
      <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.10.0/font/bootstrap-icons.css">
      <link rel="stylesheet" href="../css/transaction.css">
      <link rel="stylesheet" href="../css/main.css">

      <script src="../js/transaction.js" defer></script>
  </head>
  <body>
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
          <a href="accounts.jsp" class="nav-link">
              <i class="bi bi-credit-card"></i>
              Accounts
          </a>
          <a href="transactions.jsp" class="nav-link active">
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
              <h2 class="fw-bold"><i class="bi bi-arrow-left-right text-primary me-2"></i> Transaction Management</h2>
              <button onclick="download()" class="btn btn-primary">
                  <i class="bi bi-download me-1"></i> Export Statement
              </button>
          </div>

          <!-- Transaction Forms -->
          <div class="transaction-forms fade-in">
              <!-- Deposit Money Card -->
              <div class="card">
                  <div class="card-header">
                      <h3 class="card-title"><i class="bi bi-plus-circle"></i> Deposit Money</h3>
                  </div>
                  <form id="depositForm" class="p-3">
                      <div class="mb-3">
                          <label for="depositAccount" class="form-label">Account Number</label>
                          <select class="form-select" id="depositAccount" name="accountNumber" required>
                              <option value="">Select account...</option>
                              <option value="SAV001234567">SAV001234567 - John Smith</option>
                              <option value="SAV001234568">SAV001234568 - Sarah Johnson</option>
                              <option value="SAV001234569">SAV001234569 - Mike Davis</option>
                          </select>
                      </div>
                      <div class="mb-3">
                          <label for="depositAmount" class="form-label">Amount</label>
                          <div class="input-group">
                              <span class="input-group-text">$</span>
                              <input type="number" class="form-control" id="depositAmount" name="amount" min="1"
                                     step="0.01" required>
                          </div>
                      </div>
                      <div class="d-flex justify-content-end">
                          <button onclick="deposit()" type="submit" class="btn btn-success">
                              <i class="bi bi-plus-circle me-1"></i> Deposit
                          </button>
                      </div>
                  </form>
              </div>

              <!-- Withdraw Money Card -->
              <div class="card">
                  <div class="card-header">
                      <h3 class="card-title"><i class="bi bi-dash-circle"></i> Withdraw Money</h3>
                  </div>
                  <form id="withdrawForm" class="p-3">
                      <div class="mb-3">
                          <label for="withdrawAccount" class="form-label">Account Number</label>
                          <select class="form-select" id="withdrawAccount" name="accountNumber" required>
                              <option value="">Select account...</option>
                              <option value="SAV001234567">SAV001234567 - John Smith</option>
                              <option value="SAV001234568">SAV001234568 - Sarah Johnson</option>
                              <option value="SAV001234569">SAV001234569 - Mike Davis</option>
                          </select>
                      </div>
                      <div class="mb-3">
                          <label for="withdrawAmount" class="form-label">Amount</label>
                          <div class="input-group">
                              <span class="input-group-text">$</span>
                              <input type="number" class="form-control" id="withdrawAmount" name="amount" min="1"
                                     step="0.01" required>
                          </div>
                      </div>
                      <div class="d-flex justify-content-end">
                          <button onclick="withdraw()" type="submit" class="btn btn-danger">
                              <i class="bi bi-dash-circle me-1"></i> Withdraw
                          </button>
                      </div>
                  </form>
              </div>
          </div>

          <!-- Transaction History Card -->
          <div class="card fade-in">
              <div class="card-header">
                  <h3 class="card-title"><i class="bi bi-clock-history"></i> Transaction History</h3>
                  <div class="search-bar">
                      <i class="bi bi-search"></i>
                      <input type="text" class="form-control" placeholder="Search transactions..." id="transactionSearch">
                  </div>
              </div>
              <div class="card-body">
                  <!-- Filter Controls -->
                  <div class="filter-controls">
                      <div class="mb-3">
                          <label for="historyAccount" class="form-label">Account Number</label>
                          <select class="form-select" id="historyAccount">
                              <option value="">All accounts</option>
                              <option value="SAV001234567">SAV001234567 - John Smith</option>
                              <option value="SAV001234568">SAV001234568 - Sarah Johnson</option>
                              <option value="SAV001234569">SAV001234569 - Mike Davis</option>
                          </select>
                      </div>
                      <div class="mb-3">
                          <label for="fromDate" class="form-label">From Date</label>
                          <input type="date" class="form-control" id="fromDate">
                      </div>
                      <div class="mb-3">
                          <label for="toDate" class="form-label">To Date</label>
                          <input type="date" class="form-control" id="toDate">
                      </div>
                      <div class="mb-3 d-flex align-items-end">
                          <button class="btn btn-primary" onclick="filterTransactions()">
                              <i class="bi bi-funnel me-1"></i> Filter
                          </button>
                      </div>
                  </div>

                  <!-- Transaction Table -->
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
                          <tbody id="transactionHistoryBody">
                          <tr>
                              <td>2025-06-16</td>
                              <td>SAV001234567</td>
                              <td class="transaction-deposit">Deposit</td>
                              <td class="currency">$1,500.00</td>
                              <td class="currency">$15,250.00</td>
                          </tr>
                          <tr>
                              <td>2025-06-15</td>
                              <td>SAV001234568</td>
                              <td class="transaction-withdrawal">Withdrawal</td>
                              <td class="currency">$300.00</td>
                              <td class="currency">$8,750.00</td>
                          </tr>
                          <tr>
                              <td>2025-06-15</td>
                              <td>SAV001234569</td>
                              <td class="transaction-deposit">Deposit</td>
                              <td class="currency">$750.00</td>
                              <td class="currency">$12,500.00</td>
                          </tr>
                          <tr>
                              <td>2025-06-14</td>
                              <td>SAV001234567</td>
                              <td class="transaction-withdrawal">Withdrawal</td>
                              <td class="currency">$500.00</td>
                              <td class="currency">$13,750.00</td>
                          </tr>
                          <tr>
                              <td>2025-06-14</td>
                              <td>SAV001234568</td>
                              <td class="transaction-deposit">Deposit</td>
                              <td class="currency">$2,000.00</td>
                              <td class="currency">$9,050.00</td>
                          </tr>
                          <tr>
                              <td>2025-06-13</td>
                              <td>SAV001234569</td>
                              <td class="transaction-withdrawal">Withdrawal</td>
                              <td class="currency">$1,250.00</td>
                              <td class="currency">$11,750.00</td>
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

          <div class="modal fade" id="reportPreviewModal" tabindex="-1" aria-hidden="true">
              <div class="modal-dialog modal-lg">
                  <div class="modal-content">
                      <div class="modal-header bg-primary text-white">
                          <h5 class="modal-title">
                              <i class="bi bi-file-text me-2"></i>
                              Transaction Report - Account #70042300000138
                          </h5>
                          <button type="button" class="btn-close btn-close-white" data-bs-dismiss="modal"
                                  aria-label="Close"></button>
                      </div>
                      <div class="modal-body">
                          <div class="table-responsive">
                              <table class="table table-bordered" id="reportTable">
                                  <thead class="table-light">
                                  <tr>
                                      <th>Date</th>
                                      <th>Transaction Type</th>
                                      <th class="text-end">Amount (LKR)</th>
                                      <th class="text-end">Balance (LKR)</th>
                                  </tr>
                                  </thead>
                                  <tbody>
                                  <!-- Dummy data will be inserted here -->
                                  </tbody>
                              </table>
                          </div>
                      </div>
                      <div class="modal-footer">
                          <button type="button" class="btn btn-outline-secondary" data-bs-dismiss="modal">
                              <i class="bi bi-x-circle me-1"></i> Close
                          </button>
                          <button type="button" class="btn btn-primary" onclick="downloadPDF()">
                              <i class="bi bi-download me-1"></i> Download PDF
                          </button>
                      </div>
                  </div>
              </div>
          </div>

          <button id="viewReportBtn" class="btn btn-primary me-2">
              <i class="bi bi-eye-fill me-1"></i> View Transaction Report
          </button>
          <button id="downloadPdfBtn" class="btn btn-success">
              <i class="bi bi-file-earmark-pdf-fill me-1"></i> Download as PDF
          </button>

      </div>
  </div>

  <%-- modal wondow--%>

  <div class="modal fade" id="transactionDetailsModal" tabindex="-1" aria-hidden="true">
      <div class="modal-dialog modal-md">
          <div class="modal-content">
              <div class="modal-header" id="transactionModalHeader">
                  <h5 class="modal-title">
                      <i class="bi bi-arrow-left-right me-2"></i>
                      Transaction Details
                  </h5>
                  <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
              </div>
              <div class="modal-body">
                  <!-- Transaction Summary Card -->
                  <div class="card mb-4 border-0 shadow-sm">
                      <div class="card-body">
                          <div class="d-flex justify-content-between align-items-center mb-3">
                              <div>
                                  <h6 class="mb-1 text-muted">Transaction Type</h6>
                                  <span class="badge fs-6" id="transactionTypeBadge">Deposit</span>
                              </div>
                              <div class="text-end">
                                  <h6 class="mb-1 text-muted">Amount</h6>
                                  <h4 class="mb-0" id="transactionAmount">LKR 3,000.00</h4>
                              </div>
                          </div>
                          <hr>
                          <div class="row">
                              <div class="col-6 mb-2">
                                  <small class="text-muted">Account Number</small>
                                  <p class="mb-0 fw-bold" id="transactionAccountNumber">70042300000138</p>
                              </div>
                              <div class="col-6 mb-2 text-end">
                                  <small class="text-muted">Reference</small>
                                  <p class="mb-0 fw-bold" id="transactionReference">fff74167-2b3e...</p>
                              </div>
                              <div class="col-12 mb-2">
                                  <small class="text-muted">Description</small>
                                  <p class="mb-0 fw-bold" id="transactionDescription">My Salary</p>
                              </div>
                              <div class="col-6">
                                  <small class="text-muted">New Balance</small>
                                  <p class="mb-0 fw-bold text-success" id="transactionBalance">LKR 9,991,000.00</p>
                              </div>
                          </div>
                      </div>
                  </div>

                  <!-- Transaction Receipt -->
                  <div class="card border-0 bg-light">
                      <div class="card-body">
                          <h6 class="text-muted mb-3">
                              <i class="bi bi-receipt me-2"></i>Transaction Receipt
                          </h6>
                          <div class="d-flex justify-content-between mb-2">
                              <span class="text-muted">Date/Time:</span>
                              <span class="fw-bold" id="transactionDateTime">Jun 25, 2025 10:30 AM</span>
                          </div>
                          <div class="d-flex justify-content-between mb-2">
                              <span class="text-muted">Transaction ID:</span>
                              <span class="fw-bold" id="transactionId">fff74167-2b3e-4628-9749-1a05e1ed69fc</span>
                          </div>
                          <div class="d-flex justify-content-between">
                              <span class="text-muted">Status:</span>
                              <span class="badge bg-success">Completed</span>
                          </div>
                      </div>
                  </div>
              </div>
              <div class="modal-footer">
                  <button type="button" class="btn btn-outline-secondary" data-bs-dismiss="modal">
                      <i class="bi bi-x-circle me-1"></i> Close
                  </button>
                  <button type="button" class="btn btn-primary" onclick="printTransactionReceipt()">
                      <i class="bi bi-printer me-1"></i> Print Receipt
                  </button>
              </div>
          </div>
      </div>
  </div>

  <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
  <script src="${pageContext.request.contextPath}/js/notification.js"></script>
  <script src="https://cdnjs.cloudflare.com/ajax/libs/jspdf/2.5.1/jspdf.umd.min.js"></script>
  <script src="https://cdnjs.cloudflare.com/ajax/libs/jspdf-autotable/3.5.28/jspdf.plugin.autotable.min.js"></script>
  </body>
</html>
