package aifu.project.uhf_reader.runner;

import aifu.project.uhf_reader.service.ReaderService;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;


@Component
@RequiredArgsConstructor
public class Runner implements CommandLineRunner {
    private final ReaderService readerService;

    @Override
    public void run(String... args) {
        readerService.initReaders();
    }
}
