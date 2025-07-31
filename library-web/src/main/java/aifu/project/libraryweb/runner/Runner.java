package aifu.project.libraryweb.runner;


import aifu.project.libraryweb.runner.initializer.LuceneIndexInitializer;
import aifu.project.libraryweb.runner.initializer.SuperAdminInitializer;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class Runner implements CommandLineRunner {
    private final SuperAdminInitializer superAdminInitializer;
    private final LuceneIndexInitializer luceneIndexInitializer;


    @Override
    public void run(String... args) {
        superAdminInitializer.init();
        luceneIndexInitializer.init();
    }
}
