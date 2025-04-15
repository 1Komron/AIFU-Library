package aifu.project.librarybot.service;

import aifu.project.commondomain.entity.BookCopy;
import aifu.project.commondomain.entity.Booking;
import aifu.project.commondomain.entity.User;
import aifu.project.commondomain.entity.enums.Status;
import aifu.project.commondomain.repository.BookCopyRepository;
import aifu.project.commondomain.repository.BookingRepository;
import aifu.project.commondomain.repository.UserRepository;
import aifu.project.librarybot.exceptions.BookCopyNotFoundException;
import aifu.project.librarybot.utils.ExecuteUtil;
import aifu.project.librarybot.utils.MessageKeys;
import aifu.project.librarybot.utils.MessageUtil;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

@Service
@RequiredArgsConstructor
public class BookingService {
    private final BookingRepository bookingRepository;
    private final BookCopyRepository bookCopyRepository;
    private final UserService userService;
    private final ExecuteUtil executeUtil;
    private final TransactionalService transactionalService;
    private final UserRepository userRepository;

    @Transactional
    @SneakyThrows
    public boolean borrowBook(Long chatId, String inventoryNumber, String lang) {
        transactionalService.clearState(chatId);

        BookCopy bookCopy;
        try {
            bookCopy = bookCopyRepository.findByInventoryNumber(inventoryNumber)
                    .orElseThrow(() -> new BookCopyNotFoundException(inventoryNumber));
        } catch (BookCopyNotFoundException e) {
            SendMessage sendMessage = MessageUtil.createMessage(chatId.toString(), MessageUtil.get(MessageKeys.BOOK_NOT_FOUND, lang));
            executeUtil.execute(sendMessage);
            return false;
        }

        if (bookCopy.isTaken()) {
            SendMessage sendMessage = MessageUtil.createMessage(chatId.toString(), MessageUtil.get(MessageKeys.BOOK_ALREADY_TAKEN, lang));
            executeUtil.execute(sendMessage);
            return false;
        }

        bookCopy.setTaken(true);
        bookCopyRepository.save(bookCopy);

        User user = userRepository.findByChatId(chatId);
        bookingRepository.save(createBooking(user, bookCopy));

        return true;
    }

    public void returnBook(String inventoryNumber, Long chatId) {
    }

    public Booking createBooking(User user, BookCopy book) {
        Booking booking = new Booking();
        booking.setUser(user);
        booking.setBook(book);
        booking.setStatus(Status.WAITING_APPROVAL);
        return booking;
    }
}
