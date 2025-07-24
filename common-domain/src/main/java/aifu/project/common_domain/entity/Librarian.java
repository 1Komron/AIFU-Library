package aifu.project.common_domain.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;

@Entity
public class Librarian extends User {
    @Column(unique = true)
    private String email;

    private String password;
}

