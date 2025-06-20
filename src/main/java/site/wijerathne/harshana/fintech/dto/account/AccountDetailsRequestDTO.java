package site.wijerathne.harshana.fintech.dto.account;

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
public class AccountDetailsRequestDTO {
    private String accountNumber;
    private String accountType;
    private String customerId;
}
