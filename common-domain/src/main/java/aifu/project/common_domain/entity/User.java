package aifu.project.common_domain.entity;

import aifu.project.common_domain.entity.enums.Role;
import jakarta.persistence.*;
import lombok.Data;


@Entity
@Table(name = "users", indexes = {
        @Index(name = "idx_chatId", columnList = "chatId"),
        @Index(name = "idx_passport_code", columnList = "passportCode")
})
@Data
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String surname;

    @Enumerated(EnumType.STRING)
    private Role role = Role.USER;

    private String degree;
    private String faculty;

    @Column(unique = true, nullable = false)
    private String passportCode;

    @Column(unique = true, nullable = false)
    private String cardNumber;

    @Column(unique = true)
    private Long chatId;

    @Column(unique = true)
    private String email;
    private String password;

    private boolean isActive = false;

    private boolean isDeleted = false;

}
