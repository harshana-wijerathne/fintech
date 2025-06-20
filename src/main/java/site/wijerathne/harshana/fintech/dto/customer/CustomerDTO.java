package site.wijerathne.harshana.fintech.dto.customer;

import javax.validation.constraints.*;
import lombok.*;
import java.sql.Date;
import java.sql.Timestamp;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CustomerDTO {

    private String customerId;

    @NotBlank(message = "NIC/Passport is required")
    private String nicPassport;

    @NotBlank(message = "Full name is required")
    private String fullName;

    @NotNull(message = "Date of birth is required")
    private Date dob;

    @NotBlank(message = "Address is required")
    private String address;

    @Pattern(regexp = "^[0-9]{10}$", message = "Mobile number must be 10 digits")
    private String mobile;

    @Email(message = "Invalid email address")
    private String email;

    private Timestamp createdAt;
    private Timestamp updatedAt;
}
