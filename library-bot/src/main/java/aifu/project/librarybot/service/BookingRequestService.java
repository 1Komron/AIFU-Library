package aifu.project.librarybot.service;

import aifu.project.common_domain.entity.BookCopy;
import aifu.project.common_domain.entity.BookingRequest;
import aifu.project.common_domain.entity.User;
import aifu.project.common_domain.entity.enums.BookingRequestStatus;
import aifu.project.librarybot.repository.BookingRequestRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class BookingRequestService {
    private final BookingRequestRepository bookingRequestRepository;

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

    public boolean hasRequestForUser(Long userId) {
        return bookingRequestRepository.existsBookingRequestByUser_Id(userId);
    }

    public boolean existsRequest(BookCopy book) {
        return bookingRequestRepository.existsByBookCopy(book);
    }
}
