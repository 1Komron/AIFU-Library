package aifu.project.migrationservice;

import org.flywaydb.core.Flyway;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class MigrationServiceApplication implements CommandLineRunner {
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
        System.out.println("Запуск Flyway миграций...");

        Flyway flyway = Flyway.configure()
                .dataSource(dbUrl, dbUser, dbPassword)
                .locations("classpath:db/migration")
                .baselineOnMigrate(true) // если база уже есть
                .load();

        flyway.migrate();

        System.out.println("Миграции завершены. Сервис завершает работу.");
        System.exit(0); // обязательно завершаем приложение
    }

}
