document.addEventListener('DOMContentLoaded', function () {
    getAllCustomersForFormSelect();
    getAllAccounts()
    console.log("contected")
});



document.getElementById("accountForm").addEventListener('submit', async function(e) {
    e.preventDefault();
    const form = this;
    const formData = new FormData(form);

    const accountData = {
        customerId: formData.get('customerId').trim(),
        openingDate: formData.get('openingDate').trim(),
        accountType: formData.get('accountType'),
        balance: +formData.get('initialDeposit')
    };

    console.log(accountData)

    try{
        const response = await fetch('/admin/saving-accounts', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
                'X-CSRF-TOKEN': document.querySelector('meta[name="csrf-token"]')?.content || ''
            },
            body: JSON.stringify(accountData)
        });

        const data = await response.json();
        showAccountDetails(data);

        if(!response.ok){
            showNotification('Account registered Failed!', 'warning');
            throw new Error(data.message || 'Failed to create customer');

        }
        showNotification('Account registered successfully!', 'success');
        form.reset();
        getAllAccounts();
    }catch (error){
        console.error('Account Creation error:', error);
        showNotification(error.message || 'Error registering customer', 'error');
    }



});

window.deleteCustomer = async (id)=>{
    console.log(id)
    try{
        const response = await fetch("/admin/customers/"+id,{method:"delete"});
        console.log(response)
        if(response.ok){
            getAllCustomers();
            closeModalDelete();
            showNotification("customer Deleted Successfully" , 'success')
        }else{
            alert("failed to delete1")
        }
    }catch(error){
        alert("failed to delete2")
    }
}

window.viewAccountDetails = async (id)=>{
    try {
        const response = await fetch("/admin/saving-accounts/"+id);
        const account = await response.json()
        showAccountDetails(account);
    }catch (e){
        showNotification("Error while getting the Account details" , "warning")
    }
}

function showAccountDetails(accountData) {
    // Format the data
    const formattedDob = new Date(accountData.dob).toLocaleDateString();
    const formattedOpeningDate = new Date(accountData.openingDate).toLocaleDateString();
    const formattedBalance = new Intl.NumberFormat('en-LK', {
        style: 'currency',
        currency: 'LKR'
    }).format(accountData.balance);

    document.getElementById('customerInitial').textContent = accountData.fullName.charAt(0);
    document.getElementById('customerFullName').textContent = accountData.fullName;
    document.getElementById('accountNumberDisplay').textContent = accountData.accountNumber;
    document.getElementById('nicPassportDisplay').textContent = accountData.nicPassport;
    document.getElementById('dobDisplay').textContent = formattedDob;
    document.getElementById('openingDateDisplay').textContent = formattedOpeningDate;
    document.getElementById('addressDisplay').textContent = accountData.address;
    document.getElementById('emailDisplay').textContent = accountData.email;
    document.getElementById('balanceDisplay').textContent = formattedBalance;

    const modal = new bootstrap.Modal(document.getElementById('accountDetailsModal'));
    modal.show();


}

let lastPage = false;
const getAllAccounts = async (page , pageSize)=>{
    if(page == null) page = 1;
    if(pageSize == null) pageSize = 7;

    try {
        const response = await fetch("/admin/saving-accounts?page=" + page + "&pageSize=" + pageSize);

        if (!response.ok) {
            throw new Error(`HTTP error! status: ${response.status}`);
        }

        const accountsPage = await response.json();// Parse the JSON response
        accounts = accountsPage.content;
        const tableBody = document.getElementById("accountTableBody");

        lastPage = accounts.length < pageSize;
        if (accounts && accounts.length > 0) {
            let innerHtml = "";
            for (const account of accounts) {
                pageSize = accounts.length;
                innerHtml += `
                        <tr>
                            <td>${account.accountNumber}</td>
                            <td>${account.fullName}</td>
                            <td>${account.openingDate}</td>
                            <td class="currency">${account.balance}</td>
                            <td class="text-end">
                                <button class="btn btn-sm btn-outline-primary me-1" onclick="viewAccountDetails('${account.accountNumber}')">
                                    <i class="bi bi-eye"></i> View
                                </button>
                                <button class="btn btn-sm btn-outline-secondary">
                                    <i class="bi bi-printer"></i>
                                </button>
                            </td>
                        </tr>`;
            }

            tableBody.innerHTML = innerHtml;
        } else {
            tableBody.innerHTML = `<tr><td colspan="5">No Accounts found</td></tr>`;
        }
    } catch (error) {
        console.error("Error fetching Accounts:", error);
        document.getElementById("accountTableBody").innerHTML =
            `<tr><td colspan="5">Error loading Account Data</td></tr>`;
    }
}

window.getAllCustomersForFormSelect = async (page , pageSize) => {
    if (page == null) page = 1;
    if (pageSize == null) pageSize = 1000;

    try {
        const response = await fetch("/admin/customers?page=" + page + "&pageSize=" + pageSize);

        if (!response.ok) {
            throw new Error(`HTTP error! status: ${response.status}`);
        }

        const customers = await response.json(); // Parse the JSON response
        const selectField = document.getElementById("account-select");
        if (customers && customers.length > 0) {
            let innerHtml = "";
            for (const customer of customers) {
                pageSize = customers.length;
                innerHtml += `<option value="${customer.customerId}"> ${customer.fullName} | ${customer.nicPassport}</option>`;
            }

            selectField.innerHTML = innerHtml;
        } else {
            tableBody.innerHTML = `<option>No customers found</option>`;
        }
    } catch (error) {
        console.error("Error fetching customers:", error);
        document.getElementById("customersTableBody").innerHTML =
            `<tr><td colspan="5">Error loading customer data</td></tr>`;
    }
};

window.debounce = (func, timeout = 300)=> {
    let timer;
    return (...args) => {
        clearTimeout(timer);
        timer = setTimeout(() => { func.apply(this, args); }, timeout);
    };
}

window.searchUser = debounce(async (event) => {
    const searchTerm = event.target.value.trim();

    if (searchTerm.length === 0) {
        getAllAccounts();
    }

    try {
        const response = await fetch(`/admin/saving-accounts?key=${encodeURIComponent(searchTerm)}`);

        if (!response.ok) {
            throw new Error('Network response was not ok');
        }

        const accountsPage = await response.json();
        const accounts = accountsPage
        const tableBody = document.getElementById("accountTableBody");

        if (accounts && accounts.length > 0) {
            let innerHtml = "";
            for (const account of accounts) {
                pageSize = accounts.length;
                innerHtml += `
                        <tr>
                            <td>${account.accountNumber}</td>
                            <td>${account.fullName}</td>
                            <td>${account.openingDate}</td>
                            <td class="currency">${account.balance}</td>
                            <td class="text-end">
                                <button class="btn btn-sm btn-outline-primary me-1" onclick="viewAccountDetails('${account.accountNumber}')">
                                    <i class="bi bi-eye"></i> View
                                </button>
                                <button class="btn btn-sm btn-outline-secondary">
                                    <i class="bi bi-printer"></i>
                                </button>
                            </td>
                        </tr>`;
            }

            tableBody.innerHTML = innerHtml;
        } else {
            tableBody.innerHTML = `<tr><td colspan="5">No Accounts found</td></tr>`;
        }
    } catch (error) {
        console.error('Error fetching search results:', error);
        // You might want to show an error message to the user
    }
});


let currenPage = 1;
window.nextPage = () =>{
    console.log(currenPage)
    console.log(this.lastPage)
    if(!lastPage) {
        getAllAccounts(++currenPage)
    }
    console.log(lastPage)
    if(lastPage){
        document.getElementById("nextPage").classList.add("disabled")
    }
    else{
        document.getElementById("nextPage").classList.remove("disabled")

    }
    document.getElementById("page-link").innerText = "Page " + currenPage;
}
window.prevPage = () =>{
    getAllAccounts(currenPage<2?1:--currenPage);
    if(currenPage===1){
        document.getElementById("prevPage").classList.add("disabled")
    }else{
        document.getElementById("prevPage").classList.remove("disabled")
    }
    document.getElementById("page-link").innerText = "Page " +  currenPage+"";

}


