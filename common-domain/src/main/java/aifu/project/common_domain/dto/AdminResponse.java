package aifu.project.common_domain.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AdminResponse {
    private Long id;
    private String name;
    private String surname;
    private String email;
    private String role;
    private String imageUrl;
    private Boolean isActive;

}
