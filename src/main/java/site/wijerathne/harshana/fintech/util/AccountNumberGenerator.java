package site.wijerathne.harshana.fintech.util;

import site.wijerathne.harshana.fintech.dto.account.AccountRequestDTO;
import site.wijerathne.harshana.fintech.enums.AccountType;

import java.sql.Timestamp;
import java.util.concurrent.atomic.AtomicInteger;

public class AccountNumberGenerator {
    public static void main(String[] args) {
        AccountRequestDTO request = new AccountRequestDTO();
        request.setAccountType(AccountType.SAVING);
        request.setOpeningDate(new Timestamp(System.currentTimeMillis()));

        String accountNumber = AccountNumberGenerator.generateAccountNumber(request);
        System.out.println(accountNumber);
    }

    private static final AtomicInteger sequence = new AtomicInteger(0);

    public static String generateAccountNumber(AccountRequestDTO request) {

        String branchCode = "700";

        char typeCode = getAccountTypeCode(request.getAccountType());

        String year = String.format("%02d", request.getOpeningDate().toLocalDateTime().getYear() % 100);

        String seq = String.format("%07d", sequence.incrementAndGet() % 10_000_000);

        String partialAccount = branchCode+ typeCode + year + seq;
        char checkDigit = calculateCheckDigit(partialAccount);

        return partialAccount + checkDigit;
    }

    private static char getAccountTypeCode(AccountType type) {
        return switch (type) {
            case SAVING -> '1';
            case PREMIUM -> '2';
            case CHILDREN -> '3';
            case WOMEN -> '4';
            case SENIOR_CITIZEN -> '5';
        };
    }

    private static char calculateCheckDigit(String number) {
        int sum = 0;
        for (int i = 0; i < number.length(); i++) {
            int digit = Character.getNumericValue(number.charAt(i));
            sum += (i % 2 == 0) ? digit * 2 : digit;
        }
        return Character.forDigit((10 - (sum % 10)) % 10, 10);
    }
}
