package aifu.project.librarybot.service;

import aifu.project.commondomain.entity.BookCopy;
import aifu.project.commondomain.entity.Booking;
import aifu.project.commondomain.entity.BookingRequest;
import aifu.project.commondomain.entity.User;
import aifu.project.commondomain.entity.enums.BookingRequestStatus;
import aifu.project.commondomain.repository.BookingRequestRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
public class BookingRequestService {
    private final BookingRequestRepository bookingRequestRepository;
    private static final Logger log = LoggerFactory.getLogger(BookingRequestService.class);

    public void create(Booking booking, BookingRequestStatus status) {
        BookingRequest bookingRequest = new BookingRequest();
        bookingRequest.setUser(booking.getUser());
        bookingRequest.setBookCopy(booking.getBook());
        bookingRequest.setStatus(status);

        try {
            bookingRequestRepository.save(bookingRequest);
        }catch(Exception e) {
            log.error("Невозможно создать заявку: копия книги уже занята (bookCopyId={})", booking.getBook().getId());
        }
    }

    public void create(User user, BookCopy bookCopy, BookingRequestStatus status) {
        BookingRequest bookingRequest = new BookingRequest();
        bookingRequest.setUser(user);
        bookingRequest.setBookCopy(bookCopy);
        bookingRequest.setStatus(status);

        bookingRequestRepository.save(bookingRequest);
    }

    public void delete(BookingRequest bookingRequest) {
        bookingRequestRepository.delete(bookingRequest);
    }

    public BookingRequest getBookingResponse(Long chatId, Integer bookId, BookingRequestStatus status) {
        return bookingRequestRepository.findBookingRequestByUserChatIdAndBookCopyIdAndStatus(chatId, bookId, status);
    }
}
