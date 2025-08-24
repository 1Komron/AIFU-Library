package aifu.project.migrationservice;

import org.flywaydb.core.Flyway;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class MigrationServiceApplication implements CommandLineRunner {
    private static final Logger log = LoggerFactory.getLogger(MigrationServiceApplication.class);
    @Value("${spring.datasource.url}")
    private String dbUrl;

    @Value("${spring.datasource.username}")
    private String dbUser;

    @Value("${spring.datasource.password}")
    private String dbPassword;

    public static void main(String[] args) {
        SpringApplication.run(MigrationServiceApplication.class, args);
    }

    @Override
    public void run(String... args) {
        log.info("Migration service ishga tushirildi...");

        Flyway flyway = Flyway.configure()
                .dataSource(dbUrl, dbUser, dbPassword)
                .locations("classpath:db/migration")
                .baselineOnMigrate(true)
                .load();

        flyway.migrate();
        log.info("Migration service ishini muvaffaqiyatli yakunladi.");
        System.exit(0);
    }

}
