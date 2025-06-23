let lastPage = false;

document.addEventListener('DOMContentLoaded', async function () {
    await getAllCustomers();

});

/*---------------------------------------------------Form Submissions---------------------------------------------------*/


document.getElementById("customerForm").addEventListener('submit', async function(e) {
    e.preventDefault();
    const form = this;
    const formData = new FormData(form);

    // Reset previous errors and styles
    resetFormValidation(form);

    // Get form values
    const customerData = {
        nicPassport: formData.get('nicPassport').trim(),
        fullName: formData.get('fullName').trim(),
        dob: formData.get('dob'),
        address: formData.get('address').trim(),
        mobile: formData.get('mobile').trim(),
        email: formData.get('email').trim(),
        customerType: formData.get('customerType')
    };

    // Validate inputs
    const validation = validateCustomerData(customerData);

    if (!validation.isValid) {
        showValidationErrors(validation.errors);
        return;
    }

    // Submit data
    try {
        // Show loading state
        const submitBtn = form.querySelector('button[type="submit"]');
        const originalBtnText = submitBtn.innerHTML;
        submitBtn.innerHTML = '<span class="spinner-border spinner-border-sm" role="status" aria-hidden="true"></span> Processing...';
        submitBtn.disabled = true;

        const response = await fetch('/admin/customers', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
                'X-CSRF-TOKEN': document.querySelector('meta[name="csrf-token"]')?.content || ''
            },
            body: JSON.stringify(customerData)
        });

        const data = await response.json();

        if (!response.ok) {
            throw new Error(data.message || 'Failed to create customer');
        }

        // Success handling
        showNotification('Customer registered successfully!', 'success');
        form.reset();
        closeModal();

        // Refresh customer list
        await getAllCustomers();

    } catch (error) {
        console.error('Registration error:', error);
        showNotification(error.message || 'Error registering customer', 'error');

        // Handle specific error cases
        if (error.message.includes('NIC already exists')) {
            showError('nicPassport', 'This NIC/Passport is already registered');
        }
    } finally {
        // Reset button state
        const submitBtn = form.querySelector('button[type="submit"]');
        if (submitBtn) {
            submitBtn.innerHTML = '<i class="bi bi-save-fill me-1"></i> Register Customer';
            submitBtn.disabled = false;
        }
    }
});

// Helper functions
function resetFormValidation(form) {
    form.querySelectorAll('.form-control').forEach(input => {
        input.classList.remove('is-invalid');
        const feedback = input.nextElementSibling;
        if (feedback && feedback.classList.contains('invalid-feedback')) {
            feedback.textContent = '';
        }
    });
}

function validateCustomerData(data) {
    const errors = {};
    let isValid = true;

    // NIC/Passport validation
    if (!data.nicPassport || data.nicPassport.length < 10 || data.nicPassport.length > 12) {
        errors.nicPassport = 'NIC/Passport must be 10-12 characters';
        isValid = false;
    }

    // Full Name validation
    if (!data.fullName || data.fullName.length < 3) {
        errors.fullName = 'Full name must be at least 3 characters';
        isValid = false;
    }

    // Date of Birth validation
    if (!data.dob) {
        errors.dob = 'Date of birth is required';
        isValid = false;
    } else {
        const dobDate = new Date(data.dob);
        const minDate = new Date();
        minDate.setFullYear(minDate.getFullYear() - 120); // 120 years ago
        const maxDate = new Date();
        maxDate.setFullYear(maxDate.getFullYear() - 16); // Must be at least 16 years old

        if (dobDate < minDate || dobDate > maxDate) {
            errors.dob = 'Invalid date of birth (must be 16-120 years old)';
            isValid = false;
        }
    }

    // Address validation
    if (!data.address || data.address.length < 5) {
        errors.address = 'Address must be at least 5 characters';
        isValid = false;
    }

    // Mobile validation
    const mobilePattern = /^[0-9]{10}$/;
    if (!data.mobile || !mobilePattern.test(data.mobile)) {
        errors.mobile = 'Valid 10-digit mobile number required';
        isValid = false;
    }

    // Email validation (optional)
    if (data.email) {
        const emailPattern = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
        if (!emailPattern.test(data.email)) {
            errors.email = 'Invalid email format';
            isValid = false;
        }
    }

    return { isValid, errors };
}

function showValidationErrors(errors) {
    for (const [field, message] of Object.entries(errors)) {
        const input = document.getElementById(field);
        if (input) {
            input.classList.add('is-invalid');
            const feedback = input.nextElementSibling;
            if (feedback && feedback.classList.contains('invalid-feedback')) {
                feedback.textContent = message;
            }
        }
    }

    const firstErrorField = Object.keys(errors)[0];
    if (firstErrorField) {
        document.getElementById(firstErrorField)?.focus();
    }
}

// Demo data function
function dummyData() {
    const currentDate = new Date();
    const dobDate = new Date(currentDate.getFullYear() - 25, currentDate.getMonth(), currentDate.getDate());

    document.getElementById('fullName').value = "John Doe";
    document.getElementById('nicPassport').value = "199025600123";
    document.getElementById('dob').valueAsDate = dobDate;
    document.getElementById('address').value = "123 Main Street, Colombo";
    document.getElementById('mobile').value = "0771234567";
    document.getElementById('email').value = "john.doe@example.com";
    document.getElementById('customerType').value = "INDIVIDUAL";
    document.getElementById('customerSince').valueAsDate = new Date();
}

function showError(id, message) {
    const input = document.getElementById(id);
    input.classList.add('is-invalid');
    input.focus();
    input.nextElementSibling.textContent = message;
}


/*--------------------------------------------------Modal Window Handling-------------------------------------------------*/

/*-- Add New Cusotmer Modal*/
var addCustomerModal = new bootstrap.Modal(document.getElementById('AddNewCustomerModal'), {
    backdrop: 'static',
    keyboard: false
});
window.AddNewCustomer =() => {
    addCustomerModal.show();
};
window.closeModal = function() {
    addCustomerModal.hide();
};

/*--View Customer Modal--*/
var viewCustomerModal = new bootstrap.Modal(document.getElementById('ViewCustomerModal'), {
    backdrop: 'static',
    keyboard: false
});
window.viewCustomer =async (customerId) => {

    try {
        const response = await fetch("/admin/customers/50118b7d-cccb-498b-852e-59135835f234");
        const customer = await response.json()
        showCustomerDetails(customer);
        console.log(response)
    }catch (e){
        showNotification("Error while getting the customer" , "warn")
    }


    viewCustomerModal.show();
};
window.closeModalView = function() {
    viewCustomerModal.hide();
    const modal = bootstrap.Modal.getInstance(document.getElementById('ViewCustomerModal'));
    modal.hide();

}

/*--View Customer Modal--*/
var UpdateCustomerModal = new bootstrap.Modal(document.getElementById('UpdateCustomerModal'), {
    backdrop: 'static',
    keyboard: false
});
window.updateCustomer =() => {
    UpdateCustomerModal.show();
};
window.closeModalUpdate = function() {
    UpdateCustomerModal.hide();

}

/*--Delete Customer Modal--*/
var deleteCustomerModal = new bootstrap.Modal(document.getElementById('deleteConfirmationModal'), {
    backdrop: 'static',
    keyboard: false
});
window.deleteCustomerMod =() => {
    deleteCustomerModal.show();
};
window.closeModalDelete = function() {
    deleteCustomerModal.hide();

}

/*-------------------------------------------------------DELETE / PAGINATION / SEARCH --------------------------------------------------*/
const hi = ()=>{
    alert("hi")
}



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

window.updateCustomerTable = (customers)=> {
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
        getAllCustomers();
    }

    try {
        const response = await fetch(`/admin/customers?search=${encodeURIComponent(searchTerm)}`);

        if (!response.ok) {
            throw new Error('Network response was not ok');
        }

        const customers = await response.json();
        console.log(customers);
        displayResults(customers);
    } catch (error) {
        console.error('Error fetching search results:', error);
        // You might want to show an error message to the user
    }
});

window.displayResults=(customers)=> {
    const tableBody = document.getElementById('customersTableBody');
    tableBody.innerHTML = ''; // Clear previous results

    if (customers.length === 0) {
        tableBody.innerHTML = '<tr><td colspan="5" class="text-center">No customers found</td></tr>';
        return;
    }

    customers.forEach(customer => {
        const row = document.createElement('tr');
        row.innerHTML = `
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
                <button class="btn btn-primary" onclick="viewAccountDetails('SAV001234569')">
                      <i data-lucide="eye"></i>
                       View
                 </button>
            </td>
        `;
        tableBody.appendChild(row);
    });

    // Refresh Lucide icons if needed
    if (window.lucide) {
        window.lucide.createIcons();
    }
}

window.dummyData = () =>{
    alert("clicked")
    const currentDate = new Date();
    const dobDate = new Date(currentDate.getFullYear() - 25, currentDate.getMonth(), currentDate.getDate());
    const dobString = dobDate.toISOString().split('T')[0]; // Format as YYYY-MM-DD

    // Fill form fields with dummy data
    document.querySelector('input[name="fullName"]').value = "John Doe";
    document.querySelector('input[name="nicPassport"]').value = "199025600123";
    document.querySelector('input[name="dob"]').value = dobString;
    document.querySelector('input[name="address"]').value = "123 Main Street, Colombo";
    document.querySelector('input[name="mobile"]').value = "+94771234567";
    document.querySelector('input[name="email"]').value = "john.doe@example.com";
}

window.showNotification2 =(message, type)=>{
    const notification = document.createElement('div');
    notification.style.cssText = `
                position: fixed;
                top: 20px;
                right: 20px;
                padding: 1rem 2rem;
                border-radius: 8px;
                color: white;
                font-weight: 600;
                z-index: 10000;
                animation: slideIn 0.3s ease;
                max-width: 400px;
            `;

    switch(type) {
        case 'success':
            notification.style.background = 'linear-gradient(135deg, #28a745 0%, #20c997 100%)';
            break;
        case 'error':
            notification.style.background = 'linear-gradient(135deg, #dc3545 0%, #e83e8c 100%)';
            break;
        case 'info':
            notification.style.background = 'linear-gradient(135deg, #17a2b8 0%, #6f42c1 100%)';
            break;
        default:
            notification.style.background = 'linear-gradient(135deg, #667eea 0%, #764ba2 100%)';
    }

    notification.textContent = message;
    document.body.appendChild(notification);

    setTimeout(() => {
        notification.style.animation = 'slideOut 0.3s ease';
        setTimeout(() => {
            document.body.removeChild(notification);
        }, 300);
    }, 3000);
}

let currenPage = 1;
window.nextPage = () =>{
    console.log(currenPage)
    console.log(lastPage)
    if(!lastPage) {
        getAllCustomers(++currenPage)
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
    getAllCustomers(currenPage<2?1:--currenPage);
   if(currenPage===1){
        document.getElementById("prevPage").classList.add("disabled")
    }else{
       document.getElementById("prevPage").classList.remove("disabled")
   }
    document.getElementById("page-link").innerText = "Page " +  currenPage+"";

}


function showCustomerDetails(customerData) {
    // Format dates
    const formatDate = (dateString) => {
        if (!dateString) return 'Not available';
        try {
            return new Date(dateString).toLocaleDateString('en-US', {
                year: 'numeric',
                month: 'short',
                day: 'numeric'
            });
        } catch {
            return 'Invalid date';
        }
    };

    // Format phone number
    const formatPhone = (phone) => {
        if (!phone) return 'Not provided';
        return phone.length === 10 ? `+94${phone.substring(1)}` : phone;
    };

    // Set values or defaults
    const setValue = (elementId, value, defaultValue = 'Not provided') => {
        const element = document.getElementById(elementId);
        if (element) {
            element.textContent = value || defaultValue;
        }
    };

    // Set avatar initial
    const initial = customerData.fullName ? customerData.fullName.charAt(0).toUpperCase() : '?';
    setValue('customerInitial', initial, '?');

    // Set customer type if available (default to INDIVIDUAL)
    const customerTypeElement = document.getElementById('viewCustomerType');
    if (customerTypeElement) {
        customerTypeElement.textContent = customerData.customerType || 'INDIVIDUAL';
    }

    // Set all other values
    setValue('viewFullName', customerData.fullName);
    setValue('viewNicPassport', customerData.nicPassport);
    setValue('viewDob', formatDate(customerData.dob));
    setValue('viewCustomerSince', formatDate(customerData.createdAt));
    setValue('viewMobile', formatPhone(customerData.mobile));
    setValue('viewEmail', customerData.email);
    setValue('viewAddress', customerData.address);

    // Set account summary (if available)
    setValue('totalAccounts', customerData.totalAccounts || '0');
    setValue('totalBalance', customerData.totalBalance ?
        new Intl.NumberFormat('en-US', {
            style: 'currency',
            currency: 'USD'
        }).format(customerData.totalBalance) : '$0.00');

    // Show the modal
    const modal = new bootstrap.Modal(document.getElementById('ViewCustomerModal'));
    modal.show();
}

const getAllCustomers = async (page , pageSize) => {

    if(page == null) page = 1;
    if(pageSize == null) pageSize = 7;

    try {
        const response = await fetch("/admin/customers?page="+page+"&pageSize="+pageSize);

        if (!response.ok) {
            throw new Error(`HTTP error! status: ${response.status}`);
        }

        const customers = await response.json(); // Parse the JSON response
        const tableBody = document.getElementById("customersTableBody");
        const CustomerDeleteButton = document.getElementById("delete-modal");

        lastPage = customers.length < pageSize;

        if (customers && customers.length > 0) {
            let innerHtml = "";
            let innerHtmlCustomerDeleteButton = "";
            for (const customer of customers) {
                pageSize = customers.length;
                innerHtml += `<tr>
                                <td>${customer.fullName}</td>
                                <td>${customer.nicPassport}</td>
                                <td>${customer.mobile}</td>
                                <td>${customer.email}</td>
                                <td style="display: flex; justify-content: space-evenly">
                                    <button class="btn btn-warning" onclick="updateCustomer()">
                                        <i data-lucide="edit"></i>
                                        Edit
                                    </button>
<!--                                    <button class="btn btn-danger" onclick="deleteAddNewCustomerModal('${customer.customerId}')">-->
                                    <button class="btn btn-danger" onclick="deleteCustomerMod()">
                                        <i data-lucide="trash"></i>
                                        Delete
                                    </button>
                                    <button  class="btn btn-primary" onclick="viewCustomer('${customer.customerId}')">
                                        <i  data-lucide="eye"></i>
                                        View
                                    </button>
                                </td>
                            </tr>`;
                innerHtmlCustomerDeleteButton = `
                    <button type="button" onclick="deleteCustomer('${customer.customerId}')" class="btn btn-danger" id="confirmDeleteBtn">
                        <i class="bi bi-trash-fill me-1"></i> Delete Permanently
                    </button> 
                `
            }

            tableBody.innerHTML = innerHtml;
            CustomerDeleteButton.innerHTML = innerHtmlCustomerDeleteButton;

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







