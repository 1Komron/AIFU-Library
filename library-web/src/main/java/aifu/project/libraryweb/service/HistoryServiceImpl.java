package aifu.project.libraryweb.service;

import aifu.project.common_domain.entity.Booking;
import aifu.project.common_domain.entity.History;
import aifu.project.libraryweb.repository.HistoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class HistoryServiceImpl implements  HistoryService {
    private final HistoryRepository historyRepository;

    @Override
    public void add(Booking booking) {
        History history = new History();
        history.setUser(booking.getStudent());
        history.setBook(booking.getBook());
        history.setGivenAt(booking.getGivenAt());
        history.setDueDate(booking.getDueDate());
        history.setReturnedAt(LocalDate.now());
        historyRepository.save(history);
    }
}
