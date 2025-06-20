package site.wijerathne.harshana.fintech.util;

import site.wijerathne.harshana.fintech.dto.customer.CustomerDTO;
import site.wijerathne.harshana.fintech.dto.account.AccountRequestDTO;
import site.wijerathne.harshana.fintech.enums.AccountType;
import site.wijerathne.harshana.fintech.model.Customer;
import site.wijerathne.harshana.fintech.model.Account;

public class DTOConverter {
    public CustomerDTO convertToCustomerDTO(Customer customer) {
        return CustomerDTO.builder()
                .customerId(customer.getCustomerId())
                .nicPassport(customer.getNicPassport())
                .fullName(customer.getFullName())
                .dob(customer.getDob())
                .address(customer.getAddress())
                .mobile(customer.getMobile())
                .email(customer.getEmail())
                .createdAt(customer.getCreatedAt())
                .updatedAt(customer.getUpdatedAt())
                .build();
    }

    public Customer convertToCustomerModel(CustomerDTO customerDTO) {
        Customer customer = new Customer();
        customer.setCustomerId(customerDTO.getCustomerId());
        customer.setNicPassport(customerDTO.getNicPassport());
        customer.setFullName(customerDTO.getFullName());
        customer.setDob(customerDTO.getDob());
        customer.setAddress(customerDTO.getAddress());
        customer.setMobile(customerDTO.getMobile());
        customer.setEmail(customerDTO.getEmail());
        customer.setCreatedAt(customerDTO.getCreatedAt());
        customer.setUpdatedAt(customerDTO.getUpdatedAt());
        return customer;
    }




    public Account convertToSavingAccount(AccountRequestDTO dto) {
        if (dto == null) return null;
        return Account.builder()
                .customerId(dto.getCustomerId())
                .openingDate(dto.getOpeningDate())
                .accountType(String.valueOf(dto.getAccountType()))
                .balance(dto.getBalance())
                .build();
    }

    public AccountRequestDTO convertToSavingAccountDTO(Account account) {
        if (account == null) return null;

        return AccountRequestDTO.builder()
                .customerId(account.getCustomerId())
                .openingDate(account.getOpeningDate())
                .accountType(AccountType.valueOf(account.getAccountType()))
                .balance(account.getBalance())
                .build();
    }

}