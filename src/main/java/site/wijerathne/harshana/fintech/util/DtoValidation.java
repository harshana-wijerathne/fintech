package site.wijerathne.harshana.fintech.util;

import site.wijerathne.harshana.fintech.dto.customer.CustomerDTO;

import javax.servlet.http.HttpServletResponse;
import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.io.IOException;
import java.util.Set;

public class DtoValidation {

    public static void validate(CustomerDTO customerDTO , HttpServletResponse resp) throws IOException {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        Validator validator = factory.getValidator();

        Set<ConstraintViolation<CustomerDTO>> violations = validator.validate(customerDTO);
        if (!violations.isEmpty()) {
            StringBuilder sb = new StringBuilder();
            for (ConstraintViolation<CustomerDTO> violation : violations) {
                sb.append(violation.getMessage()).append("; ");
            }
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, sb.toString());
            throw new IOException(sb.toString());
        }
    }


}
