package site.wijerathne.harshana.fintech.service.customer;

import site.wijerathne.harshana.fintech.dto.customer.CustomerDTO;

import java.sql.Connection;
import java.util.List;

public interface CustomerService {

    List<CustomerDTO> getAllCustomers(int page, int pageSize);

    CustomerDTO getCustomerById(String customerId);

    List<CustomerDTO> searchCustomers(String searchTerm);

    CustomerDTO createCustomer(CustomerDTO customerDTO, String actorUserId, String ipAddress);

    void deleteCustomer(String customerId, String actorUserId, String ipAddress);

    CustomerDTO updateCustomer(CustomerDTO customerDTO, String actorUserId, String ipAddress);
}
