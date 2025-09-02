package aifu.project.uhf_reader.service;

import aifu.project.uhf_reader.connection.ReaderConnection;
import aifu.project.uhf_reader.repository.BookCopyRepository;
import aifu.project.uhf_reader.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class ReaderService {
    private final RabbitTemplate rabbitTemplate;
    private final BookCopyRepository bookCopyRepository;
    private final NotificationRepository notificationRepository;
    private final BookingService bookingService;

    public void initReaders() {
        ReaderConnection reader1 = new ReaderConnection(8085, "Reader-1",
                rabbitTemplate, bookCopyRepository, notificationRepository, bookingService);

        reader1.initReader();

        log.info("Reader 1 init qilinishi.");

        ReaderConnection reader2 = new ReaderConnection(8085, "Reader-2",
                rabbitTemplate, bookCopyRepository, notificationRepository, bookingService);

        log.info("Reader 2 init qilinishi.");

        reader2.initReader();
    }
}


