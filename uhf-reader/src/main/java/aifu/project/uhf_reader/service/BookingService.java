package aifu.project.uhf_reader.service;

import aifu.project.uhf_reader.repository.BookingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BookingService {
    private final BookingRepository bookingRepository;

    public int isEpcBooked(String epc) {
        return bookingRepository.getBookEpcStatus(epc);
    }
}
