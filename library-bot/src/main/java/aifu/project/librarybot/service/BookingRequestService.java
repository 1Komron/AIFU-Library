package aifu.project.librarybot.service;

import aifu.project.commondomain.entity.BookCopy;
import aifu.project.commondomain.entity.BookingRequest;
import aifu.project.commondomain.entity.User;
import aifu.project.commondomain.entity.enums.BookingRequestStatus;
import aifu.project.librarybot.repository.BookingRequestRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
public class BookingRequestService {
    private final BookingRequestRepository bookingRequestRepository;
    private static final Logger log = LoggerFactory.getLogger(BookingRequestService.class);

    public BookingRequest create(User user, BookCopy bookCopy, BookingRequestStatus status) {
        BookingRequest bookingRequest = new BookingRequest();
        bookingRequest.setUser(user);
        bookingRequest.setBookCopy(bookCopy);
        bookingRequest.setStatus(status);

        try {
            return bookingRequestRepository.save(bookingRequest);
        } catch (Exception e) {
            log.error("Unable to create a request: a copy of the book is already taken (bookCopyId={})", bookCopy.getId());
            throw new RuntimeException("Unable to create a request: a copy of the book is already taken");
        }
    }

    public void delete(BookingRequest bookingRequest) {
        bookingRequestRepository.delete(bookingRequest);
    }

    public BookingRequest getBookingRequest(Long chatId, Integer bookCopyId, BookingRequestStatus status) {
        return bookingRequestRepository.findBookingRequestByUserChatIdAndBookCopyIdAndStatus(chatId, bookCopyId, status);
    }
}
