package aifu.project.uhf_reader.runner;

import aifu.project.uhf_rfid.service.RfidService;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;


@Component
@RequiredArgsConstructor
public class Runner implements CommandLineRunner {
    private final RfidService rfidService;

    @Override
    public void run(String... args) {
        rfidService.initReader();
    }
}
