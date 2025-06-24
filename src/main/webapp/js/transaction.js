function download() {
    generateReport();
    downloadPDF();
}

const transactions = [
    {date: '2025-06-01', type: 'Deposit', amount: 10000.00, balance: 10000.00},
    {date: '2025-06-05', type: 'Withdrawal', amount: 2500.00, balance: 7500.00},
    {date: '2025-06-10', type: 'Deposit', amount: 5000.00, balance: 12500.00},
    {date: '2025-06-15', type: 'Transfer', amount: 3000.00, balance: 9500.00},
    {date: '2025-06-20', type: 'Deposit', amount: 15000.00, balance: 24500.00},
    {date: '2025-06-25', type: 'Withdrawal', amount: 5000.00, balance: 19500.00}
];

function formatCurrency(amount) {
    return new Intl.NumberFormat('en-LK', {
        minimumFractionDigits: 2,
        maximumFractionDigits: 2
    }).format(amount);
}

function generateReport() {
    const tbody = document.querySelector('#reportTable tbody');
    tbody.innerHTML = '';

    transactions.forEach(txn => {
        const row = document.createElement('tr');
        row.innerHTML = `
            <td>${txn.date}</td>
            <td>${txn.type}</td>
            <td class="text-end ${txn.type == 'Deposit' ? 'text-success' : 'text-danger'}">
                ${txn.type == 'Deposit' ? '+' : '-'}${formatCurrency(txn.amount)}
            </td>
            <td class="text-end">${formatCurrency(txn.balance)}</td>
        `;
        tbody.appendChild(row);
    });
}

document.getElementById('viewReportBtn').addEventListener('click', function () {
    generateReport();
    const modal = new bootstrap.Modal(document.getElementById('reportPreviewModal'));
    modal.show();
});

function downloadPDF() {
    const {jsPDF} = window.jspdf;
    const doc = new jsPDF();

    // Title
    doc.setFontSize(16);
    doc.setTextColor(40, 53, 147);
    doc.text('Transaction Report - Account #70042300000138', 14, 20);

    // Date
    doc.setFontSize(10);
    doc.setTextColor(100, 100, 100);
    doc.text(`Generated on: 2025-06-21`, 14, 28);

    // Table
    const headers = [['Date', 'Transaction Type', 'Amount (LKR)', 'Balance (LKR)']];
    const data = transactions.map(txn => [
        txn.date,
        txn.type,
        (txn.type == 'Deposit' ? '+' : '-') + formatCurrency(txn.amount),
        formatCurrency(txn.balance)
    ]);

    doc.autoTable({
        head: headers,
        body: data,
        startY: 35,
        styles: {
            fontSize: 9,
            cellPadding: 3,
            valign: 'middle'
        },
        columnStyles: {
            2: {halign: 'right', fontStyle: 'bold'},
            3: {halign: 'right'}
        },
        didDrawCell: (data) => {
            if (data.column.index === 2 && data.cell.raw[0] === '+') {
                data.cell.styles.textColor = [40, 167, 69]; // Green for deposits
            } else if (data.column.index === 2 && data.cell.raw[0] === '-') {
                data.cell.styles.textColor = [220, 53, 69]; // Red for withdrawals
            }
        }
    });

    // Footer
    const pageCount = doc.internal.getNumberOfPages();
    for (let i = 1; i <= pageCount; i++) {
        doc.setPage(i);
        doc.setFontSize(8);
        doc.setTextColor(150);
        doc.text(`Page ${i} of ${pageCount}`, doc.internal.pageSize.width - 25, doc.internal.pageSize.height - 10);
    }

    doc.save(`Transaction_Report.pdf`);
}

// Direct download button
document.getElementById('downloadPdfBtn').addEventListener('click', function () {
    generateReport();
    downloadPDF();
});

function showTransactionDetails(transactionData, isDeposit = true) {
    // Format currency
    const formatCurrency = (amount) => {
        return new Intl.NumberFormat('en-LK', {
            style: 'currency',
            currency: 'LKR'
        }).format(amount);
    };

    const header = document.getElementById('transactionModalHeader');
    header.className = isDeposit
        ? 'modal-header bg-success text-white'
        : 'modal-header bg-danger text-white';

    // Set transaction type badge
    const typeBadge = document.getElementById('transactionTypeBadge');
    typeBadge.className = isDeposit
        ? 'badge fs-6 bg-success bg-opacity-10 text-success'
        : 'badge fs-6 bg-danger bg-opacity-10 text-danger';
    typeBadge.textContent = isDeposit ? 'Deposit' : 'Withdrawal';

    // Format and set values
    document.getElementById('transactionAmount').textContent = formatCurrency(transactionData.amount);
    document.getElementById('transactionAccountNumber').textContent = transactionData.accountNumber;
    document.getElementById('transactionReference').textContent = transactionData.referenceNumber.substring(0, 8) + '...';
    document.getElementById('transactionDescription').textContent = transactionData.description || 'No description';
    document.getElementById('transactionBalance').textContent = formatCurrency(transactionData.balance);
    document.getElementById('transactionId').textContent = transactionData.referenceNumber;

    // Set current date/time for receipt
    document.getElementById('transactionDateTime').textContent = new Date().toLocaleString('en-US', {
        year: 'numeric',
        month: 'short',
        day: 'numeric',
        hour: '2-digit',
        minute: '2-digit'
    });

    // Show the modal
    const modal = new bootstrap.Modal(document.getElementById('transactionDetailsModal'));
    modal.show();
}

// Print receipt function
function printTransactionReceipt() {
    showNotification('Receipt sent to printer', 'success');
}

function deposit() {
    // showTransactionDetails({
    //     accountNumber: "70042300000138",
    //     amount: 3000,
    //     balance: 9991000.00,
    //     description: "My Salary",
    //     referenceNumber: "fff74167-2b3e-4628-9749-1a05e1ed69fc"
    // }, true);
}

document.getElementById("depositForm").addEventListener("submit", function (e) {
    e.preventDefault();
    const form = this;
    const formData = new FormData(form);
    console.log(formData)


    fetch("/admin/saving-accounts", {method: 'POST', body: JSON.stringify(formData)})
        .then(result => result.json())
        .then(result => {
            showTransactionDetails(result);
        }).catch(err => {
        showNotification("Transaction Failed", "error")
    })

})

function withdraw() {
    showTransactionDetails({
        accountNumber: "70042300000138",
        amount: 5000,
        balance: 9986000.00,
        description: "ATM Withdrawal",
        referenceNumber: "abc74167-2b3e-4628-9749-1a05e1ed69fc"
    }, false);
}


document.addEventListener('DOMContentLoaded', function() {
    let accounts = [];

    window.getAccountDetails = function() {
        fetch("/admin/saving-accounts")
            .then(response => response.json())
            .then(data => {
                accounts = data.content;
                initializeAccountSelector();
            })
            .catch(error => {
                console.error("Error fetching accounts:", error);
                showNotification('Failed to load accounts', 'error');
            });
    }

    function initializeAccountSelector() {
        const form = document.getElementById('depositForm');
        const selectElement = document.getElementById('depositAccount');
        const dropdownToggle = document.querySelector('.account-selector-toggle');
        const selectedAccountText = document.querySelector('.selected-account-text');
        const accountListContainer = document.querySelector('.account-list-container');
        const searchInput = document.querySelector('.account-search');

        // Clear and disable the native select initially
        selectElement.innerHTML = '<option value="">Select account...</option>';
        selectElement.required = true;

        function renderAccountList(filter = '') {
            accountListContainer.innerHTML = '';

            const filteredAccounts = accounts.filter(account =>
                account.accountNumber.toLowerCase().includes(filter.toLowerCase()) ||
                (account.customerName && account.customerName.toLowerCase().includes(filter.toLowerCase()))
            );

            if (filteredAccounts.length === 0) {
                accountListContainer.innerHTML = `
                    <div class="text-center py-3 text-muted">
                        <i class="bi bi-people-slash"></i> No accounts found
                    </div>
                `;
                return;
            }

            filteredAccounts.forEach(account => {
                const accountItem = document.createElement('div');
                accountItem.className = 'account-item';
                accountItem.innerHTML = `
                    <div>
                        <div class="account-number">${account.accountNumber}</div>
                        <div class="account-name">${account.customerName || 'No name'}</div>
                    </div>
                    <div class="account-balance">${formatCurrency(account.balance || 0)}</div>
                `;

                accountItem.addEventListener('click', function() {
                    // Update both the hidden select and the display
                    selectElement.value = account.accountNumber;
                    selectedAccountText.textContent = `${account.accountNumber} - ${account.customerName || ''}`;

                    // Clear any validation errors
                    selectElement.classList.remove('is-invalid');
                    dropdownToggle.classList.remove('is-invalid');

                    // Close the dropdown
                    const dropdown = bootstrap.Dropdown.getInstance(dropdownToggle);
                    if (dropdown) dropdown.hide();
                });

                accountListContainer.appendChild(accountItem);
            });
        }

        // Form submission validation
        form.addEventListener('submit', function(event) {
            if (!selectElement.value) {
                event.preventDefault();
                selectElement.classList.add('is-invalid');
                dropdownToggle.classList.add('is-invalid');
                showNotification('Please select an account', 'error');
            }
        });

        // Search functionality
        searchInput.addEventListener('input', function() {
            renderAccountList(this.value);
        });

        // Initial render
        renderAccountList();
    }

    function formatCurrency(amount) {
        return new Intl.NumberFormat('en-US', {
            style: 'currency',
            currency: 'USD'
        }).format(amount);
    }

    // Initialize on page load
    getAccountDetails();
});






