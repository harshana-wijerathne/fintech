package site.wijerathne.harshana.fintech.model;

import lombok.*;

import java.sql.Timestamp;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class User {
    private String userId;
    private String username;
    private String fullName;
    private String password;
    private String email;
    private String role;
    private Timestamp createdAt;
}
