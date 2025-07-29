package aifu.project.uhf_reader;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EntityScan("aifu.project.common_domain.entity")
@EnableAsync
public class UhfReaderApplication {

    public static void main(String[] args) {
        SpringApplication.run(UhfReaderApplication.class, args);
    }

}
