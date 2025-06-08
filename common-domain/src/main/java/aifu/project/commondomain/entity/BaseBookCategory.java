package aifu.project.commondomain.entity;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class BaseBookCategory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY  )
    private Integer id;

    @Column(nullable = false, unique = true)
    private String name;
}
