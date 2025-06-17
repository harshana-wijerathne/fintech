document.addEventListener('DOMContentLoaded', function () {
    getAllCustomers();
});

const getAllCustomers = async () => {
    try {
        const response = await fetch("http://localhost:8080/admin/customers");

        if (!response.ok) {
            throw new Error(`HTTP error! status: ${response.status}`);
        }

        const customers = await response.json(); // Parse the JSON response
        const tableBody = document.getElementById("customersTableBody");

        if (customers && customers.length > 0) {
            let innerHtml = "";

            // Use for...of loop for arrays instead of for...in
            for (const customer of customers) {
                innerHtml += `<tr>
                                <td>${customer.fullName}</td>
                                <td>${customer.nicPassport}</td>
                                <td>${customer.mobile}</td>
                                <td>${customer.email}</td>
                                <td>
                                    <button class="btn btn-warning" onclick="editCustomer(${customer.id})">
                                        <i data-lucide="edit"></i>
                                        Edit
                                    </button>
                                    <button class="btn btn-danger" onclick="deleteCustomer(${customer.id})">
                                        <i data-lucide="trash"></i>
                                        Delete
                                    </button>
                                </td>
                            </tr>`;
            }

            tableBody.innerHTML = innerHtml;

            // Refresh Lucide icons after DOM update
            if (window.lucide) {
                lucide.createIcons();
            }
        } else {
            tableBody.innerHTML = `<tr><td colspan="5">No customers found</td></tr>`;
        }
    } catch (error) {
        console.error("Error fetching customers:", error);
        document.getElementById("customersTableBody").innerHTML =
            `<tr><td colspan="5">Error loading customer data</td></tr>`;
    }
};

const deleteCustomer = async (id)=>{
    try{
        const response = await fetch("http://localhost:8080/admin/customers?id="+id,{method:"delete"});
        if(response.ok){
            getAllCustomers();
        }else{
            alert("failed to delete1")
        }
    }catch(error){
        alert("failed to delete2")
    }
}


function searchUser(e) {
    console.log(e)
}

// Function to update the table with search results
function updateCustomerTable(customers) {
    const tableBody = document.getElementById('customersTableBody');

    if (!customers || customers.length === 0) {
        tableBody.innerHTML = '<tr><td colspan="5">No customers found</td></tr>';
        return;
    }

    let html = '';
    customers.forEach(customer => {
        html += `
        <tr>
            <td>${customer.fullName}</td>
            <td>${customer.nicPassport}</td>
            <td>${customer.mobile}</td>
            <td>${customer.email}</td>
            <td>
                <button class="btn btn-warning" onclick="editCustomer(${customer.id})">
                    <i data-lucide="edit"></i>
                    Edit
                </button>
                <button class="btn btn-danger" onclick="deleteCustomer(${customer.id})">
                    <i data-lucide="trash"></i>
                    Delete
                </button>
            </td>
        </tr>`;
    });

    tableBody.innerHTML = html;

    // Refresh Lucide icons
    if (window.lucide) {
        lucide.createIcons();
    }

}