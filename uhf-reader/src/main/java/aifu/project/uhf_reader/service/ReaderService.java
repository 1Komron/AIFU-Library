package aifu.project.uhf_reader.service;

import aifu.project.uhf_reader.connection.ReaderConnection;
import aifu.project.uhf_reader.repository.BookCopyRepository;
import aifu.project.uhf_reader.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

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

        ReaderConnection reader2 = new ReaderConnection(8086, "Reader-2",
                rabbitTemplate, bookCopyRepository, notificationRepository, bookingService);

        reader2.initReader();
    }
}


