package aifu.project.libraryweb.runner;


import aifu.project.libraryweb.runner.initializer.LuceneIndexInitializer;
import aifu.project.libraryweb.runner.initializer.SuperAdminInitializer;
//import aifu.project.libraryweb.runner.migration.MigrationConstraint;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class Runner implements CommandLineRunner {
    private final SuperAdminInitializer superAdminInitializer;
    private final LuceneIndexInitializer luceneIndexInitializer;
   // private final MigrationConstraint migrationConstraint;


    @Override
    public void run(String... args) {
     //   migrationConstraint.migrate();
        superAdminInitializer.init();
        luceneIndexInitializer.init();
    }
}
