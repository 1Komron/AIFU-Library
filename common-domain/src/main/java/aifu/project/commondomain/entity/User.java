package aifu.project.commondomain.entity;

import aifu.project.commondomain.entity.enums.Role;
import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "users", indexes = {
        @Index(name = "idx_chatId", columnList = "chatId")
})
@Data
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String surname;
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

    private Long chatId;

    boolean isActive = false;
}
