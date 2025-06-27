package aifu.project.common_domain.entity;

import aifu.project.common_domain.entity.enums.Role;
import jakarta.persistence.*;
import lombok.Data;

import java.io.Serializable;

@Entity
@Table(name = "users", indexes = {
        @Index(name = "idx_chatId", columnList = "chatId")
})
@Data
public class User implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String surname;
    @Column(unique = true)
    private String phone;

    @Enumerated(EnumType.STRING)
    private Role role = Role.USER;

    private String faculty;
    private String course;
    @Column(name = "group_number")
    private String group;

    @Column(unique = true)
    private String email;
    private String password;

    @Column(unique = true)
    private Long chatId;

    private boolean isActive = false;

    private boolean isDeleted = false;
}
