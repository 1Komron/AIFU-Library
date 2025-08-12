package aifu.project.common_domain.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import lombok.*;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Librarian extends User {

    @Column(nullable = false)
    private String email;

    @Column(nullable = false)
    private String password;

}

