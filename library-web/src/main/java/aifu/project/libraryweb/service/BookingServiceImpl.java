package aifu.project.libraryweb.service;

import aifu.project.common_domain.dto.BookingShortDTO;
import aifu.project.common_domain.dto.BookingSummaryDTO;
import aifu.project.common_domain.dto.BorrowBookDTO;
import aifu.project.common_domain.dto.ReturnBookDTO;
import aifu.project.common_domain.entity.BookCopy;
import aifu.project.common_domain.entity.Booking;
import aifu.project.common_domain.entity.User;
import aifu.project.common_domain.entity.enums.Status;
import aifu.project.common_domain.exceptions.BookingNotFoundException;
import aifu.project.common_domain.payload.ResponseMessage;
import aifu.project.libraryweb.repository.BookingRepository;
import aifu.project.libraryweb.service.base_book_service.BookCopyService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {
    private final BookingRepository bookingRepository;
    private final UserService userService;
    private final BookCopyService bookCopyService;
    private final HistoryService historyService;

    @Override
    public ResponseEntity<ResponseMessage> getBookingList(int pageNum, int pageSize) {
        Pageable pageable = PageRequest.of(--pageNum, pageSize);
        Page<BookingShortDTO> page = bookingRepository.findAllBookingShortDTO(pageable);

        Map<String, Object> data = Map.of(
                "data", page.getContent(),
                "currentPage", page.getNumber() + 1,
                "totalPages", page.getTotalPages(),
                "totalElements", page.getTotalElements()
        );

        return ResponseEntity.ok(new ResponseMessage(true, "data", data));
    }

    @Override
    public ResponseEntity<ResponseMessage> getBooking(Long id) {
        BookingSummaryDTO data = bookingRepository.findSummary(id)
                .orElseThrow(() -> new BookingNotFoundException("Booking not found. By id: " + id));

        return ResponseEntity.ok(new ResponseMessage(true, "Booking by id: " + id, data));
    }

    @Override
    public ResponseEntity<ResponseMessage> filterByStatus(String status, int pageNum, int pageSize) {
        Pageable pageable = PageRequest.of(--pageNum, pageSize);
        Status statusEnum = Status.getStatus(status);

        Page<BookingShortDTO> page = bookingRepository.findShortByStatus(statusEnum, pageable);

        Map<String, Object> data = Map.of(
                "data", page.getContent(),
                "currentPage", page.getNumber() + 1,
                "totalPages", page.getTotalPages(),
                "totalElements", page.getTotalElements()
        );

        return ResponseEntity.ok(new ResponseMessage(true, "data", data));
    }

    @Override
    public ResponseEntity<ResponseMessage> borrowBook(BorrowBookDTO request) {
        String cardNumber = request.cardNumber();
        String epc = request.epc();

        User user = userService.findByCardNumber(cardNumber);

        BookCopy bookCopy = bookCopyService.findByEpc(epc);

        createBooking(user, bookCopy);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ResponseMessage(true, "Booking created successfully", null));
    }

    @Override
    public ResponseEntity<ResponseMessage> returnBook(ReturnBookDTO request) {
        String cardNumber = request.cardNumber();
        String epc = request.epc();

        User user = userService.findByCardNumber(cardNumber);

        BookCopy bookCopy = bookCopyService.findByEpc(epc);

        Booking booking = bookingRepository.findByUserAndBook(user, bookCopy)
                .orElseThrow(() -> new BookingNotFoundException("Booking not found for user: " + user.getId() + " and book copy: " + bookCopy.getId()));

        bookCopyService.updateStatus(bookCopy);

        historyService.add(booking);

        bookingRepository.delete(booking);

        return ResponseEntity.ok(new ResponseMessage(true, "Booking returned successfully", null));
    }

    public void createBooking(User user, BookCopy bookCopy) {
        Booking booking = new Booking();
        booking.setUser(user);
        booking.setBook(bookCopy);
        bookingRepository.save(booking);
    }
}
