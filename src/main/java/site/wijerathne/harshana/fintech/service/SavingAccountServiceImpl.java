package site.wijerathne.harshana.fintech.service;

import site.wijerathne.harshana.fintech.repo.SavingAccountRepo;
import site.wijerathne.harshana.fintech.model.SavingAccount;
import site.wijerathne.harshana.fintech.util.DTOConverter;

import java.sql.Connection;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class SavingAccountServiceImpl implements SavingAccountService {
    private static final Logger logger = Logger.getLogger(SavingAccountServiceImpl.class.getName());
    private final SavingAccountRepo savingAccountRepo;
    private final DTOConverter dtoConverter;

    public SavingAccountServiceImpl() {
        this.savingAccountRepo = new SavingAccountRepo();
        this.dtoConverter = new DTOConverter();
    }

    @Override
    public SavingAccount getSavingAccountById(String accountNumber, Connection connection) {
        try {
            return savingAccountRepo.getSavingAccountById(accountNumber, connection);
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Failed to get saving account", e);
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<SavingAccount> getAllSavingAccounts(Connection connection) {
        try {
            return savingAccountRepo.getAllSavingAccounts(connection);
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Failed to get all saving accounts", e);
            throw new RuntimeException(e);
        }
    }
}

