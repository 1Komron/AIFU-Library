package aifu.project.librarybot.service;

import aifu.project.commondomain.entity.Booking;
import aifu.project.commondomain.entity.History;
import aifu.project.commondomain.repository.HistoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class HistoryService {
    private final HistoryRepository historyRepository;

    public void add(Booking booking) {
        History history = new History();
        history.setUser(history.getUser());
        history.setBook(history.getBook());

        historyRepository.save(history);
    }

}
