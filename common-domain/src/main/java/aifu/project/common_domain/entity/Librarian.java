package aifu.project.common_domain.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import lombok.Data;

@Entity
@Data
public class Librarian extends User {

    @Column(unique = true,nullable = false)
    private String email;

    @Column(nullable = false)
    private String password;

}

