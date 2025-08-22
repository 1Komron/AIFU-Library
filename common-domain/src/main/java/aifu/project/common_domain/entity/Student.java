package aifu.project.common_domain.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import lombok.Data;

import java.time.LocalDate;

@Entity
@Data
@Table(indexes = {
        @Index(name = "idx_chatId", columnList = "chatId"),
        @Index(name = "idx_passport_code", columnList = "passportCode")
})
public class Student extends User {

    private String degree;

    private String faculty;

    private String passportCode;

    @Column(nullable = false)
    private String cardNumber;

    private LocalDate admissionTime;

    private LocalDate graduationTime;

    private String phoneNumber;

    @Column(unique = true)
    private Long chatId;

}

