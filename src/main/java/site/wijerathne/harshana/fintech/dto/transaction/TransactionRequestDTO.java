package site.wijerathne.harshana.fintech.dto.transaction;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Data
public class TransactionRequestDTO {
    private String accountNumber;
    private BigDecimal amount;
    private String description;

}