package aifu.project.common_domain.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;

@Entity
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

