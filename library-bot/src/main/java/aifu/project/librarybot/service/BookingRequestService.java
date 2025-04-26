package aifu.project.librarybot.service;

import aifu.project.commondomain.entity.BookCopy;
import aifu.project.commondomain.entity.Booking;
import aifu.project.commondomain.entity.BookingRequest;
import aifu.project.commondomain.entity.User;
import aifu.project.commondomain.entity.enums.BookingRequestStatus;
import aifu.project.commondomain.repository.BookingRequestRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
public class BookingRequestService {
    private final BookingRequestRepository bookingRequestRepository;

    public void create(Booking booking, BookingRequestStatus status) {
        BookingRequest bookingRequest = new BookingRequest();
        bookingRequest.setUser(booking.getUser());
        bookingRequest.setBookCopy(booking.getBook());
        bookingRequest.setStatus(status);

        bookingRequestRepository.save(bookingRequest);
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
