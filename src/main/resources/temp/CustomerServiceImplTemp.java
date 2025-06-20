package temp;

import site.wijerathne.harshana.fintech.repo.AuditLogRepo;
import site.wijerathne.harshana.fintech.repo.customer.CustomerRepo;
import site.wijerathne.harshana.fintech.dto.AuditLogDTO;
import site.wijerathne.harshana.fintech.dto.customer.CustomerDTO;
import site.wijerathne.harshana.fintech.model.Customer;
import site.wijerathne.harshana.fintech.util.DTOConverter;

import java.sql.SQLException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;


public class CustomerServiceImpl implements CustomerService {
    private static final Logger logger = Logger.getLogger(CustomerServiceImpl.class.getName());
    private final CustomerRepo customerRepo;
    private final AuditLogRepo auditLogRepo;
    private final DTOConverter dtoConverter;


    public CustomerServiceImpl(CustomerRepo customerRepo, AuditLogRepo auditLogRepo) {
        this.customerRepo = customerRepo;
        this.auditLogRepo = auditLogRepo;
        this.dtoConverter = new DTOConverter();
    }

    public List<CustomerDTO> getAllCustomers(int page, int pageSize) {
        try {
            return customerRepo.getAllCustomers(page, pageSize)
                    .stream()
                    .map(dtoConverter::convertToCustomerDTO)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error fetching all customers", e);
            throw new RuntimeException("Error fetching customers", e);
        }
    }

    public CustomerDTO getCustomerById(String customerId) {
        try {
            Customer customer = customerRepo.getCustomerById(customerId);
            return customer != null ? dtoConverter.convertToCustomerDTO(customer) : null;
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error fetching customer by ID: " + customerId, e);
            throw new RuntimeException("Error fetching customer", e);
        }
    }

    public List<CustomerDTO> searchCustomers(String searchTerm) {
        try {
            return customerRepo.findCustomersByNameOrNIC(searchTerm)
                    .stream()
                    .map(dtoConverter::convertToCustomerDTO)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error searching customers with term: " + searchTerm, e);
            throw new RuntimeException("Error searching customers", e);
        }
    }

    public CustomerDTO createCustomer(CustomerDTO customerDTO, String actorUserId, String ipAddress) {
        try {
            Customer customer = dtoConverter.convertToCustomerModel(customerDTO);
            Customer createdCustomer = customerRepo.saveCustomer(customer);

            // Log audit
            AuditLogDTO auditLog = AuditLogDTO.builder()
                    .actorUserId(actorUserId)
                    .actionType("CREATE")
                    .entityType("CUSTOMER")
                    .entityId(createdCustomer.getCustomerId())
                    .description("Created new customer: " + createdCustomer.getFullName() + "|" + createdCustomer.getNicPassport())
                    .ipAddress(ipAddress)
                    .build();
            auditLogRepo.saveAuditLog(auditLog);

            return dtoConverter.convertToCustomerDTO(createdCustomer);
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error creating customer", e);
            throw new RuntimeException("Error creating customer", e);
        }
    }

    public void deleteCustomer(String customerId, String actorUserId, String ipAddress) {
        try {

            Customer customer = customerRepo.getCustomerById(customerId);
            if (customer == null) {
                throw new IllegalArgumentException("Customer not found with ID: " + customerId);
            }


            boolean deleted = customerRepo.deleteCustomer(customerId);
            if (!deleted) {
                throw new RuntimeException("Failed to delete customer with ID: " + customerId);
            }

            AuditLogDTO auditLog = AuditLogDTO.builder()
                    .actorUserId(actorUserId)
                    .actionType("DELETE")
                    .entityType("CUSTOMER")
                    .entityId(customerId)
                    .description("Deleted customer: " + customer.getFullName())
                    .ipAddress(ipAddress)
                    .build();
            auditLogRepo.saveAuditLog(auditLog);

        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error deleting customer with ID: " + customerId, e);
            throw new RuntimeException("Error deleting customer", e);
        }
    }

    public CustomerDTO updateCustomer(CustomerDTO customerDTO, String actorUserId, String ipAddress) {
        try {
            String customerId = customerDTO.getCustomerId();
            Customer existingCustomer = customerRepo.getCustomerById(customerDTO.getCustomerId());
            if (existingCustomer == null) {
                throw new IllegalArgumentException("Customer not found with ID: " + customerId);
            }

            Customer customerToUpdate = dtoConverter.convertToCustomerModel(customerDTO);
            customerToUpdate.setCustomerId(customerId);

            Customer updatedCustomer = customerRepo.updateCustomer(customerToUpdate);

            AuditLogDTO auditLog = AuditLogDTO.builder()
                    .actorUserId(actorUserId)
                    .actionType("UPDATE")
                    .entityType("CUSTOMER")
                    .entityId(customerId)
                    .description("Updated customer: " + updatedCustomer.getFullName())
                    .ipAddress(ipAddress)
                    .build();
            auditLogRepo.saveAuditLog(auditLog);

            return dtoConverter.convertToCustomerDTO(updatedCustomer);

        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error updating customer with ID: " + customerDTO.getCustomerId(), e);
            throw new RuntimeException("Error updating customer", e);
        }
    }
}
