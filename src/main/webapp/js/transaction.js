const {jsPDF} = window.jspdf;
let dpdf = new jsPDF();
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

document.getElementById('downloadPdfBtn').addEventListener('click', function () {
    generateReport();
    downloadPDF();
});

function showTransactionDetails(transactionData, isDeposit = true) {

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


    const typeBadge = document.getElementById('transactionTypeBadge');
    typeBadge.className = isDeposit
        ? 'badge fs-6 bg-success bg-opacity-10 text-success'
        : 'badge fs-6 bg-danger bg-opacity-10 text-danger';
    typeBadge.textContent = isDeposit ? 'Deposit' : 'Withdrawal';


    document.getElementById('transactionAmount').textContent = formatCurrency(transactionData.amount);
    document.getElementById('transactionAccountNumber').textContent = transactionData.accountNumber;
    document.getElementById('transactionReference').textContent = transactionData.referenceNumber.substring(25,36 );
    document.getElementById('transactionDescription').textContent = transactionData.description || 'No description';
    document.getElementById('transactionBalance').textContent = formatCurrency(transactionData.balance);
    document.getElementById('transactionId').textContent = transactionData.referenceNumber;
    document.getElementById('transactionId').textContent = transactionData.referenceNumber.substring(25,36 )


    document.getElementById('transactionDateTime').textContent = new Date().toLocaleString('en-US', {
        year: 'numeric',
        month: 'short',
        day: 'numeric',
        hour: '2-digit',
        minute: '2-digit'
    });


    const modal = new bootstrap.Modal(document.getElementById('transactionDetailsModal'));
    modal.show();
}

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

    // Convert FormData to a plain object
    const data = {};
    formData.forEach((value, key) => {
        data[key] = value;
    });

    // Convert amount to number (since FormData stores all values as strings)
    data.amount = parseFloat(data.amount);

    fetch("/admin/transactions", {
        method: 'POST',
        body: JSON.stringify(data),
        headers: {
            'Content-Type': 'application/json'
        },
    })
        .then(response => {
            if (!response.ok) {
                throw new Error('Network response was not ok');
            }
            return response.json();
        })
        .then(result => {
            showTransactionDetails(result);
            showNotification("Deposit successful", "success");
            loadAndFilterTransactions();
        })
        .catch(err => {
            console.error('Error:', err);
            showNotification("Transaction Failed", "error");
        });
});

document.getElementById("withdrawForm").addEventListener("submit", function (e) {
    e.preventDefault();
    const form = this;
    const formData = new FormData(form);

    const data = {};
    formData.forEach((value, key) => {
        data[key] = value;
    });

    // Convert amount to number
    data.amount = parseFloat(data.amount);

    fetch("/admin/transactions", {
        method: 'PUT',
        body: JSON.stringify(data),
        headers: {
            'Content-Type': 'application/json'
        },
    })
        .then(async response => {
            if (!response.ok) {
                let errorMsg = 'Transaction failed';
                try {
                    const errorResult = await response.json();
                    errorMsg = errorResult.message || errorResult.error || errorMsg;
                } catch (e) {
                    // If we can't parse JSON, use status text
                    errorMsg = response.statusText || errorMsg;
                }
                throw new Error(errorMsg);
            }

            try {
                const result = await response.json();
                showNotification("Withdraw successful", "success");
                showTransactionDetails(result,false);
            } catch (e) {
                showNotification("Withdraw Failed", "error");
            }
        })
        .catch(err => {
            console.error('Error:', err);
            showNotification(err.message || "Transaction Failed", "error");
        });
});

function withdraw() {
    // showTransactionDetails({
    //     accountNumber: "7004230000013",
    //     amount: 5000,
    //     balance: 9986000.00,
    //     description: "ATM Withdrawal",
    //     referenceNumber: "abc74167-2b3e-4628-9749-1a05e1ed69fc"
    // }, false);
}

window.getAllAccountsForFormSelect = async (page , pageSize) => {
    console.log("getAllAccountsForFormSelect")
    if (page == null) page = 1;
    if (pageSize == null) pageSize = 1000;

    try {
        const response = await fetch("/admin/saving-accounts?page=" + page + "&pageSize=" + pageSize);

        if (!response.ok) {
            throw new Error(`HTTP error! status: ${response.status}`);
        }

        const customers = await response.json();
        const selectFieldWITH = document.getElementById("withdrawAccountSelect");
        const selectFieldDEP = document.getElementById("depositAccountFormSelect");
        const selectFieldHistory = document.getElementById("historyAccount");
        if (customers && customers.content.length > 0) {
            let innerHtml = `<option>Select Account...</option>`;
            for (const customer of customers.content) {
                pageSize = customers.length;
                innerHtml += `<option value="${customer.accountNumber}"> ${customer.fullName} | ${customer.accountNumber}</option>`;
            }

            selectFieldWITH.innerHTML = innerHtml;
            selectFieldDEP.innerHTML = innerHtml;
            selectFieldHistory.innerHTML = innerHtml;
        } else {
            selectFieldWITH.innerHTML = `<option>No customers found</option>`;
            selectFieldDEP.innerHTML = `<option>No customers found</option>`;
            selectFieldHistory.innerHTML = `<option>Please Select an account number</option>`;
        }
    } catch (error) {
        console.error("Error fetching customers:", error);
    }
};

document.addEventListener('DOMContentLoaded', function() {
    console.log("conected")
    // let accounts = [];
    //
    // window.getAccountDetails = function() {
    //     fetch("/admin/saving-accounts")
    //         .then(response => response.json())
    //         .then(data => {
    //             accounts = data.content;
    //             initializeAccountSelector();
    //         })
    //         .catch(error => {
    //             console.error("Error fetching accounts:", error);
    //             showNotification('Failed to load accounts', 'error');
    //         });
    // }
    //
    // function initializeAccountSelector() {
    //     const form = document.getElementById('depositForm');
    //     const selectElement = document.getElementById('depositAccount');
    //     const dropdownToggle = document.querySelector('.account-selector-toggle');
    //     const selectedAccountText = document.querySelector('.selected-account-text');
    //     const accountListContainer = document.querySelector('.account-list-container');
    //     const searchInput = document.querySelector('.account-search');
    //
    //     // Clear and disable the native select initially
    //     selectElement.innerHTML = '<option value="">Select account...</option>';
    //     selectElement.required = true;
    //
    //     function renderAccountList(filter = '') {
    //         accountListContainer.innerHTML = '';
    //
    //         const filteredAccounts = accounts.filter(account =>
    //             account.accountNumber.toLowerCase().includes(filter.toLowerCase()) ||
    //             (account.customerName && account.customerName.toLowerCase().includes(filter.toLowerCase()))
    //         );
    //
    //         if (filteredAccounts.length === 0) {
    //             accountListContainer.innerHTML = `
    //                 <div class="text-center py-3 text-muted">
    //                     <i class="bi bi-people-slash"></i> No accounts found
    //                 </div>
    //             `;
    //             return;
    //         }
    //
    //         filteredAccounts.forEach(account => {
    //             const accountItem = document.createElement('div');
    //             accountItem.className = 'account-item';
    //             accountItem.innerHTML = `
    //                 <div>
    //                     <div class="account-number">${account.accountNumber}</div>
    //                     <div class="account-name">${account.customerName || 'No name'}</div>
    //                 </div>
    //                 <div class="account-balance">${formatCurrency(account.balance || 0)}</div>
    //             `;
    //
    //             accountItem.addEventListener('click', function() {
    //                 // Update both the hidden select and the display
    //                 selectElement.value = account.accountNumber;
    //                 selectedAccountText.textContent = `${account.accountNumber} - ${account.customerName || ''}`;
    //
    //                 // Clear any validation errors
    //                 selectElement.classList.remove('is-invalid');
    //                 dropdownToggle.classList.remove('is-invalid');
    //
    //                 // Close the dropdown
    //                 const dropdown = bootstrap.Dropdown.getInstance(dropdownToggle);
    //                 if (dropdown) dropdown.hide();
    //             });
    //
    //             accountListContainer.appendChild(accountItem);
    //         });
    //     }
    //
    //     // Form submission validation
    //     form.addEventListener('submit', function(event) {
    //         if (!selectElement.value) {
    //             event.preventDefault();
    //             selectElement.classList.add('is-invalid');
    //             dropdownToggle.classList.add('is-invalid');
    //             showNotification('Please select an account', 'error');
    //         }
    //     });
    //
    //     // Search functionality
    //     searchInput.addEventListener('input', function() {
    //         renderAccountList(this.value);
    //     });
    //
    //     // Initial render
    //     renderAccountList();
    // }
    //
    // function formatCurrency(amount) {
    //     return new Intl.NumberFormat('en-US', {
    //         style: 'currency',
    //         currency: 'USD'
    //     }).format(amount);
    // }

    getAllAccountsForFormSelect();
});

let allAccountTransactions = [];

async function loadAndFilterTransactions() {
    const accountNumber = document.getElementById('historyAccount').value;
    const fromDate = document.getElementById('fromDate').value;
    const toDate = document.getElementById('toDate').value;
    const transactionHistoryBody = document.getElementById('transactionHistoryBody');

    // Clear table
    transactionHistoryBody.innerHTML = '';

    if (!accountNumber) {
        transactionHistoryBody.innerHTML = '<tr><td colspan="5" class="text-center">Please select an account number</td></tr>';
        return;
    }

    try {
        // Show loading
        transactionHistoryBody.innerHTML = '<tr><td colspan="5" class="text-center">Loading transactions...</td></tr>';

        // 1. Get transactions for the selected account
        if (allAccountTransactions.length === 0) {
            const accountResponse = await fetch(`/admin/transactions/${accountNumber}`);
            if (!accountResponse.ok) throw new Error('Failed to load account transactions');
            const accountData = await accountResponse.json();

            allAccountTransactions = Array.isArray(accountData) ? accountData :
                accountData.content || accountData.transactions || [];

            console.log('Account transactions:', allAccountTransactions);
        }


        let filteredTransactions = [...allAccountTransactions];

        if (fromDate || toDate) {
            filteredTransactions = filteredTransactions.filter(transaction => {
                const transDate = transaction.transactionDate || transaction.createdAt;
                if (!transDate) return false;

                const transactionDate = new Date(transDate);
                const from = fromDate ? new Date(fromDate) : null;
                const to = toDate ? new Date(toDate) : null;

                // Check if transaction is within date range
                return (!from || transactionDate >= from) &&
                    (!to || transactionDate <= new Date(to.getTime() + 86400000)); // Add 1 day to include toDate
            });
        }

        console.log('Filtered transactions:', filteredTransactions);

        // 3. Display results
        if (filteredTransactions.length === 0) {
            transactionHistoryBody.innerHTML = '<tr><td colspan="5" class="text-center">No matching transactions found</td></tr>';
            return;
        }

        renderTransactions(filteredTransactions);
        generateReport(filteredTransactions);
        generatedPDF(filteredTransactions)

    } catch (error) {
        generateReport([]);
        console.error('Error:', error);
        transactionHistoryBody.innerHTML = `<tr><td colspan="5" class="text-center text-danger">Error: ${error.message}</td></tr>`;
    }
}

function renderTransactions(transactions) {
    const transactionHistoryBody = document.getElementById('transactionHistoryBody');

    if (!Array.isArray(transactions)) {
        console.error('Transactions is not an array:', transactions);
        transactionHistoryBody.innerHTML = '<tr><td colspan="5" class="text-center">Invalid transaction data</td></tr>';
        return;
    }

    let html = '';

    transactions.forEach(transaction => {
        if (!transaction || !transaction.transactionType || !transaction.amount) {
            console.warn('Invalid transaction:', transaction);
            return;
        }

        const typeClass = transaction.transactionType.toLowerCase() === 'deposit'
            ? 'transaction-deposit'
            : 'transaction-withdrawal';

        const amountSign = transaction.transactionType.toLowerCase() === 'deposit' ? '+' : '-';
        const formattedAmount = `${amountSign}LKR${Math.abs(transaction.amount).toFixed(2)}`;

        html += `
            <tr>
                <td>${transaction.createdAt || 'N/A'}</td>
                <td>${transaction.accountNumber || 'N/A'}</td>
                <td class="${typeClass}">${transaction.transactionType || 'N/A'}</td>
                <td class="currency">${formattedAmount}</td>
                <td class="currency">LKR${(transaction.balance || 0).toFixed(2)}</td>
            </tr>
        `;
    });

    transactionHistoryBody.innerHTML = html || '<tr><td colspan="5" class="text-center">No transactions to display</td></tr>';
}

document.getElementById('historyAccount').addEventListener('change', async function() {
    // Clear cached transactions when account changes
    allAccountTransactions = [];
    await loadAndFilterTransactions();
});

document.getElementById('fromDate').addEventListener('change', loadAndFilterTransactions);
document.getElementById('toDate').addEventListener('change', loadAndFilterTransactions);
document.addEventListener('DOMContentLoaded', () => {
    loadAndFilterTransactions();
});

function generateReport(transactions) {
    const tbody = document.querySelector('#reportTable tbody');
    tbody.innerHTML = '';
    this.transactions = transactions;
    transactions.forEach(txn => {
        const row = document.createElement('tr');
        row.innerHTML = `
            <td>${txn.createdAt}</td>
            <td>${txn.transactionType}</td>
            <td class="text-end ${txn.transactionType.toLowerCase() == 'deposit' ? 'text-success' : 'text-danger'}">
                ${txn.transactionType.toLowerCase() == 'Deposit' ? '+' : '-'}${formatCurrency(txn.amount)}
            </td>
            <td class="text-end">${formatCurrency(txn.balance)}</td>
        `;
        tbody.appendChild(row);
    });
}

document.getElementById('viewReportBtn').addEventListener('click', function () {
    const modal = new bootstrap.Modal(document.getElementById('reportPreviewModal'));
    modal.show();
});

function generatedPDF(transactions) {
    const {jsPDF} = window.jspdf;
    const doc = new jsPDF();

    // Title
    doc.setFontSize(16);
    doc.setTextColor(40, 53, 147);
    doc.text(`Transaction Report - Account No: ${transactions[1].accountNumber}`, 14, 20);

    // Date
    doc.setFontSize(10);
    doc.setTextColor(100, 100, 100);
    doc.text(`Generated on: 2025-06-21`, 14, 28);

    // Table
    const headers = [['Date', 'Transaction Type', 'Amount (LKR)', 'Balance (LKR)']];
    const data = transactions.map(txn => [
        txn.createdAt,
        txn.transactionType,
        (txn.transactionType == 'Deposit' ? '+' : '-') + formatCurrency(txn.amount),
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

    dpdf = doc;
}

function downloadPDF() {
    dpdf.save(`Transaction_Report.pdf`);
}

document.addEventListener('DOMContentLoaded', () => {
    loadAndFilterTransactions();
});



