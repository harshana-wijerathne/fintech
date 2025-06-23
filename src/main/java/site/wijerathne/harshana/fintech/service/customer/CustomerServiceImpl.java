package site.wijerathne.harshana.fintech.service.customer;

import site.wijerathne.harshana.fintech.exception.customer.*;
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
            throw new CustomerServiceException("Error fetching customers", e);
        }
    }

    public CustomerDTO getCustomerById(String customerId) {
        try {
            Customer customer = customerRepo.getCustomerById(customerId);
            if (customer == null) {
                logger.log(Level.WARNING, "Customer not found with ID: " + customerId);
                throw new CustomerNotFoundException(customerId);
            }
            return dtoConverter.convertToCustomerDTO(customer);
        } catch (CustomerNotFoundException e) {
            throw e;
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error fetching customer by ID: " + customerId, e);
            throw new CustomerServiceException("Error fetching customer", e);
        }
    }

    public List<CustomerDTO> searchCustomers(String searchTerm) {
        try {
            if (searchTerm == null || searchTerm.trim().isEmpty()) {
                logger.log(Level.WARNING, "Empty search term provided");
                throw new IllegalArgumentException("Search term cannot be empty");
            }

            List<Customer> customers = customerRepo.findCustomersByNameOrNIC(searchTerm);
            return customers.stream()
                    .map(dtoConverter::convertToCustomerDTO)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error searching customers with term: " + searchTerm, e);
            throw new CustomerServiceException("Error searching customers", e);
        }
    }

    public CustomerDTO createCustomer(CustomerDTO customerDTO, String actorUserId, String ipAddress) {
        try {
            if (customerDTO == null) {
                throw new IllegalArgumentException("Customer data cannot be null");
            }

            Customer customer = dtoConverter.convertToCustomerModel(customerDTO);

            boolean customerExist = customerRepo.existsCustomer(customerDTO.getNicPassport());
            if(customerExist) throw new CustomerAlreadyExisException("Customer already exists");
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
        }catch (CustomerAlreadyExisException e){
            logger.log(Level.WARNING, "NIC is already exist", e);
            throw new CustomerAlreadyExisException("NIC is already exist");
        }
        catch (SQLException e) {
            logger.log(Level.SEVERE, "Error creating customer", e);
            throw new CustomerCreationException("Error creating customer", e);
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Unexpected error creating customer", e);
            throw new CustomerCreationException("Unexpected error creating customer", e);
        }
    }

    public void deleteCustomer(String customerId, String actorUserId, String ipAddress) {
        try {
            Customer customer = customerRepo.getCustomerById(customerId);
            if (customer == null) {
                logger.log(Level.WARNING, "Customer not found with ID: " + customerId);
                throw new CustomerNotFoundException(customerId);
            }

            boolean deleted = customerRepo.deleteCustomer(customerId);
            if (!deleted) {
                logger.log(Level.SEVERE, "Failed to delete customer with ID: " + customerId);
                throw new CustomerDeletionException("Failed to delete customer with ID: " + customerId);
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
            throw new CustomerDeletionException("Error deleting customer", e);
        }
    }

    public CustomerDTO updateCustomer(CustomerDTO customerDTO, String actorUserId, String ipAddress) {
        try {
            if (customerDTO == null) {
                throw new IllegalArgumentException("Customer data cannot be null");
            }

            String customerId = customerDTO.getCustomerId();
            Customer existingCustomer = customerRepo.getCustomerById(customerId);
            if (existingCustomer == null) {
                logger.log(Level.WARNING, "Customer not found with ID: " + customerId);
                throw new CustomerNotFoundException(customerId);
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

        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error updating customer with ID: " + customerDTO.getCustomerId(), e);
            throw new CustomerUpdateException("Error updating customer", e);
        } catch (CustomerNotFoundException e) {
            throw e;
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Unexpected error updating customer", e);
            throw new CustomerUpdateException("Unexpected error updating customer", e);
        }
    }
}