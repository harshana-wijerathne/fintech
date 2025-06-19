package site.wijerathne.harshana.fintech.repo;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import site.wijerathne.harshana.fintech.model.SavingAccount;

import java.math.BigDecimal;
import java.sql.*;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class SavingAccountRepoTest {

    private Connection mockConnection;
    private PreparedStatement mockPreparedStatement;
    private ResultSet mockResultSet;
    private SavingAccountRepo dao;

    @BeforeEach
    public void setUp() throws SQLException {
        mockConnection = mock(Connection.class);
        mockPreparedStatement = mock(PreparedStatement.class);
        mockResultSet = mock(ResultSet.class);
        dao = new SavingAccountRepo();

        when(mockConnection.prepareStatement(any(String.class))).thenReturn(mockPreparedStatement);
    }

    @Test
    public void testGetSavingAccountById_ReturnsAccount() throws SQLException {
        when(mockPreparedStatement.executeQuery()).thenReturn(mockResultSet);
        when(mockResultSet.next()).thenReturn(true);
        when(mockResultSet.getString("account_number")).thenReturn("ACC001");
        when(mockResultSet.getString("customer_id")).thenReturn("CUS001");
        when(mockResultSet.getTimestamp("opening_date")).thenReturn(Timestamp.valueOf("2024-01-01 10:00:00"));
        when(mockResultSet.getString("account_type")).thenReturn("STANDARD");
        when(mockResultSet.getBigDecimal("balance")).thenReturn(new BigDecimal("1000.00"));
        when(mockResultSet.getTimestamp("created_at")).thenReturn(Timestamp.valueOf("2024-01-01 10:00:00"));
        when(mockResultSet.getTimestamp("updated_at")).thenReturn(Timestamp.valueOf("2024-01-01 10:00:00"));

        SavingAccount account = dao.getSavingAccountById("ACC001", mockConnection);
        assertNotNull(account);
        assertEquals("ACC001", account.getAccountNumber());
        assertEquals("CUS001", account.getCustomerId());
    }

    @Test
    public void testSaveSavingAccount_Success() throws SQLException {
        SavingAccount account = SavingAccount.builder()
                .accountNumber("ACC002")
                .customerId("CUS002")
                .openingDate(Timestamp.valueOf("2024-01-01 12:00:00"))
                .accountType("PREMIUM")
                .balance(new BigDecimal("5000.00"))
                .createdAt(Timestamp.valueOf("2024-01-01 12:00:00"))
                .updatedAt(Timestamp.valueOf("2024-01-01 12:00:00"))
                .build();

        when(mockPreparedStatement.executeUpdate()).thenReturn(1);
        when(mockConnection.getAutoCommit()).thenReturn(true);

        boolean result = dao.saveSavingAccount(account, mockConnection);
        assertTrue(result);
        verify(mockConnection).commit();
    }

    @Test
    public void testUpdateSavingAccount_Failure() throws SQLException {
        SavingAccount account = SavingAccount.builder()
                .accountNumber("ACC999")
                .customerId("CUS999")
                .openingDate(Timestamp.valueOf("2024-01-01 12:00:00"))
                .accountType("BASIC")
                .balance(new BigDecimal("200.00"))
                .updatedAt(Timestamp.valueOf("2024-01-01 12:00:00"))
                .build();

        when(mockPreparedStatement.executeUpdate()).thenReturn(0); // No rows updated
        when(mockConnection.getAutoCommit()).thenReturn(true);

        boolean result = dao.updateSavingAccount(account, mockConnection);
        assertFalse(result);
        verify(mockConnection).commit();
    }

    @Test
    public void testDeleteSavingAccount_Success() throws SQLException {
        when(mockPreparedStatement.executeUpdate()).thenReturn(1);
        when(mockConnection.getAutoCommit()).thenReturn(true);

        boolean result = dao.deleteSavingAccount("ACC003", mockConnection);
        assertTrue(result);
        verify(mockConnection).commit();
    }

    @Test
    public void testSearchSavingAccountsByPartialAccountNumber_ReturnsList() throws SQLException {
        when(mockPreparedStatement.executeQuery()).thenReturn(mockResultSet);
        when(mockResultSet.next()).thenReturn(true, false);
        when(mockResultSet.getString("account_number")).thenReturn("ACC004");
        when(mockResultSet.getString("customer_id")).thenReturn("CUS004");
        when(mockResultSet.getTimestamp("opening_date")).thenReturn(Timestamp.valueOf("2024-01-01 12:00:00"));
        when(mockResultSet.getString("account_type")).thenReturn("PREMIUM");
        when(mockResultSet.getBigDecimal("balance")).thenReturn(new BigDecimal("3000.00"));
        when(mockResultSet.getTimestamp("created_at")).thenReturn(Timestamp.valueOf("2024-01-01 12:00:00"));
        when(mockResultSet.getTimestamp("updated_at")).thenReturn(Timestamp.valueOf("2024-01-01 12:00:00"));

        List<SavingAccount> accounts = dao.searchSavingAccountsByAccountNumber("ACC", mockConnection);
        assertEquals(1, accounts.size());
        assertEquals("ACC004", accounts.get(0).getAccountNumber());
    }
}

