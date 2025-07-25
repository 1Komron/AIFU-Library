package aifu.project.libraryweb.config;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "importer.column-aliases")
@Getter
@Setter

public class ImporterColumnProperties {
    private String passportCode = "passportcode";
    private String surname = "surname";
    private String name = "name";
    private String degree = "degree";
    private String faculty = "faculty";
    private String cardNumber = "card rifd";
}