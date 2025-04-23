package aifu.project.librarybot.service;

import aifu.project.commondomain.entity.BaseBook;
import aifu.project.commondomain.entity.BookCopy;
import aifu.project.commondomain.entity.Booking;
import aifu.project.commondomain.entity.History;
import aifu.project.commondomain.repository.HistoryRepository;
import aifu.project.librarybot.utils.MessageKeys;
import aifu.project.librarybot.utils.MessageUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
@RequiredArgsConstructor
public class HistoryService {
    private final HistoryRepository historyRepository;

    public void add(Booking booking) {
        History history = new History();
        history.setUser(booking.getUser());
        history.setBook(booking.getBook());
        history.setGivenAt(booking.getGivenAt());
        history.setDueDate(booking.getDueDate());
        history.setReturnedAt(LocalDateTime.now());

        historyRepository.save(history);
    }

    public String getHistory(Long chatId, String lang) {
        List<History> allHistories = historyRepository.findAllByUser_ChatId(chatId);
        if (allHistories == null || allHistories.isEmpty())
            return MessageUtil.get(MessageKeys.BOOKING_HISTORY_EMPTY, lang);

        StringBuilder messageText = new StringBuilder();

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        String template = MessageUtil.get(MessageKeys.BOOKING_HISTORY, lang);

        allHistories.forEach(h ->
                {
                    BookCopy book = h.getBook();
                    BaseBook baseBook = book.getBook();

                    String givenAt = h.getGivenAt().format(formatter);
                    String dueDate = h.getDueDate().format(formatter);
                    String returnedDate = h.getReturnedAt().format(formatter);

                    messageText.append(String.format(template,
                            book.getInventoryNumber(),
                            baseBook.getAuthor(),
                            baseBook.getTitle(),
                            givenAt,
                            dueDate,
                            returnedDate
                    ));

                    messageText.append("\n\n");
                }
        );

        return messageText.toString();
    }

}
