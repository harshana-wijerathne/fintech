package site.wijerathne.harshana.fintech.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.sql.Timestamp;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SavingAccountDTO {
    private String customerId;
    private Timestamp openingDate;
    private String accountType;
    private BigDecimal balance;
}
