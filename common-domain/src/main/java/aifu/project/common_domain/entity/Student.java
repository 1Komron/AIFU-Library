package aifu.project.common_domain.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Data
@Table(indexes = {
        @Index(name = "idx_chatId", columnList = "chatId"),
        @Index(name = "idx_passport_code", columnList = "passportCode")
})
public class Student extends User {

    private String degree;

    private String faculty;

    @Column(unique = true, nullable = false)
    private String passportCode;

    @Column(unique = true, nullable = false)
    private String cardNumber;

    @Column(unique = true)
    private Long chatId;

}

