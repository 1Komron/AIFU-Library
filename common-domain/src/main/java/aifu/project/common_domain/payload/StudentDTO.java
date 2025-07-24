package aifu.project.common_domain.payload;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class StudentDTO {
    String name;
    String surname;
    String faculty;
    String degree;
    String cardNumber;
    Long chatId;
}
