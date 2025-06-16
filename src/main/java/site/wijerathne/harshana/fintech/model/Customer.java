package site.wijerathne.harshana.fintech.model;

import lombok.Data;

import java.util.Date;

@Data
public class Customer {
    int id;
    String fullName;
    String nicPassport;
    Date dob;
    String address;
    String mobile;
    String email;
}
