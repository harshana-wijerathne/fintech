function download(){
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

document.addEventListener('DOMContentLoaded', function () {
    console.log("great")
});

function greate() {
    alert("clicked")
}


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
    // In a real app, this would generate a printable receipt
    console.log('Printing receipt...');
    showNotification('Receipt sent to printer', 'success');
}

function deposit() {
    showTransactionDetails({
        accountNumber: "70042300000138",
        amount: 3000,
        balance: 9991000.00,
        description: "My Salary",
        referenceNumber: "fff74167-2b3e-4628-9749-1a05e1ed69fc"
    }, true);
}

function withdraw() {
    showTransactionDetails({
        accountNumber: "70042300000138",
        amount: 5000,
        balance: 9986000.00,
        description: "ATM Withdrawal",
        referenceNumber: "abc74167-2b3e-4628-9749-1a05e1ed69fc"
    }, false);
}

