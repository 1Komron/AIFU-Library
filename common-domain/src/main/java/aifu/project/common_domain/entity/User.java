package aifu.project.common_domain.entity;

import aifu.project.common_domain.entity.enums.Role;
import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
@Inheritance(strategy = InheritanceType.JOINED)
@Table(name = "users")
public abstract class User {
    @Id
    @GeneratedValue(strategy =  GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String surname;

    @Enumerated(EnumType.STRING)
    private Role role;

    private boolean isDeleted = false;

    private boolean isActive = false;

}
