package site.wijerathne.harshana.fintech.service;

import site.wijerathne.harshana.fintech.dto.SavingAccountDTO;
import site.wijerathne.harshana.fintech.model.SavingAccount;

import java.sql.Connection;
import java.util.List;

public interface SavingAccountService {
    SavingAccount getSavingAccountById(String accountNumber, Connection connection);
    List<SavingAccount> getAllSavingAccounts(Connection connection);
}

