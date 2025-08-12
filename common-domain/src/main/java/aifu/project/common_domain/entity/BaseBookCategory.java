package aifu.project.common_domain.entity;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Builder
public class BaseBookCategory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY  )
    private Integer id;

    @Column(nullable = false)
    private String name;

    @Builder.Default
    private boolean isDeleted = false;

}
