package aifu.project.librarybot.service;

import aifu.project.commondomain.entity.BaseBook;
import aifu.project.commondomain.entity.BookCopy;
import aifu.project.commondomain.entity.Booking;
import aifu.project.commondomain.entity.History;
import aifu.project.commondomain.payload.PartList;
import aifu.project.librarybot.repository.HistoryRepository;
import aifu.project.librarybot.utils.ExecuteUtil;
import aifu.project.librarybot.utils.MessageKeys;
import aifu.project.librarybot.utils.MessageUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
@RequiredArgsConstructor
public class HistoryService {
    private final HistoryRepository historyRepository;
    private final ExecuteUtil executeUtil;

    public void add(Booking booking) {
        History history = new History();
        history.setUser(booking.getUser());
        history.setBook(booking.getBook());
        history.setGivenAt(booking.getGivenAt());
        history.setDueDate(booking.getDueDate());
        history.setReturnedAt(LocalDate.now());

        historyRepository.save(history);
    }

    public PartList getHistory(Long chatId, String lang, int pageNumber) {
        Pageable pageable = PageRequest.of(--pageNumber, 3);
        Page<History> historyPage = historyRepository.findAllByUserChatId(chatId, pageable);
        List<History> allHistories = historyPage.getContent();

        if (allHistories.isEmpty()) {
            executeUtil.executeMessage(chatId.toString(), MessageKeys.BOOKING_HISTORY_EMPTY, lang);
            return null;
        }

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

        return new PartList(messageText.toString(), ++pageNumber, historyPage.getTotalPages());
    }

}
