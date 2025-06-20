package site.wijerathne.harshana.fintech.model;

import lombok.*;

import java.math.BigDecimal;
import java.sql.Timestamp;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Account {
    private String accountNumber;
    private String customerId;
    private Timestamp openingDate;
    private String accountType;
    private BigDecimal balance;
    private Timestamp createdAt;
    private Timestamp updatedAt;

    @Override
    public String toString() {
        return "Account{" +
                "accountNumber='" + accountNumber + '\'' +
                ", customerId='" + customerId + '\'' +
                ", openingDate=" + openingDate +
                ", accountType='" + accountType + '\'' +
                ", balance=" + balance +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                '}';
    }
}

