package aifu.project.librarybot;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;

@SpringBootApplication
@EntityScan(basePackages = "aifu.project.commondomain")
public class LibraryBotApplication {

    public static void main(String[] args) {
        SpringApplication.run(LibraryBotApplication.class, args);
    }
}
