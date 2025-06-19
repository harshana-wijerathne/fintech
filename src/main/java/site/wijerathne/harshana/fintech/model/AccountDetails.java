package site.wijerathne.harshana.fintech.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.sql.Date;
import java.sql.Timestamp;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AccountDetails {
    private String accountNumber;
    private Timestamp openingDate;
    private String accountType;
    private BigDecimal balance;
    private Timestamp accountCreatedAt;
    private Timestamp accountUpdatedAt;

    private String customerId;
    private String fullName;
    private String nicPassport;
    private Date dob;
    private String address;
    private String mobileNo;
    private String email;
    private Timestamp customerCreatedAt;
    private Timestamp customerUpdatedAt;
}
