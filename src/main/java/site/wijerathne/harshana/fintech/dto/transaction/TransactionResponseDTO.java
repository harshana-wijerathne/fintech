package site.wijerathne.harshana.fintech.dto.transaction;


import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.LocalDateTime;

@Setter
@Getter
public class TransactionResponseDTO {
    private String transactionId;
    private String accountNumber;
    private BigDecimal amount;
    private BigDecimal balance;
    private String transactionType;
    private String description;
    private String referenceNumber;
    private Timestamp createdAt;

}