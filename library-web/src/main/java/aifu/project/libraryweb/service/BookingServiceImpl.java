package aifu.project.libraryweb.service;

import aifu.project.common_domain.dto.*;
import aifu.project.common_domain.entity.BookCopy;
import aifu.project.common_domain.entity.Booking;
import aifu.project.common_domain.entity.Student;
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

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {
    private final BookingRepository bookingRepository;
    private final StudentService studentService;
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

        Student student = studentService.findByCardNumber(cardNumber);

        BookCopy bookCopy = bookCopyService.findByEpc(epc);

        createBooking(student, bookCopy);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ResponseMessage(true, "Booking created successfully", null));
    }

    @Override
    public ResponseEntity<ResponseMessage> returnBook(ReturnBookDTO request) {
        String cardNumber = request.cardNumber();
        String epc = request.epc();

        Student student = studentService.findByCardNumber(cardNumber);

        BookCopy bookCopy = bookCopyService.findByEpc(epc);

        Booking booking = bookingRepository.findByStudentAndBook(student, bookCopy)
                .orElseThrow(() -> new BookingNotFoundException("Booking not found for student: " + student.getId() + " and book copy: " + bookCopy.getId()));

        bookCopyService.updateStatus(bookCopy);

        historyService.add(booking);

        bookingRepository.delete(booking);

        return ResponseEntity.ok(new ResponseMessage(true, "Booking returned successfully", null));
    }

    public void createBooking(Student student, BookCopy bookCopy) {
        Booking booking = new Booking();
        booking.setStudent(student);
        booking.setBook(bookCopy);
        bookingRepository.save(booking);
    }

    @Override
    public long countAllBookings() {
        return bookingRepository.count();
    }

    @Override
    public BookingDiagramDTO getBookingDiagram() {
        return bookingRepository.getDiagramData();
    }

    @Override
    public List<BookingResponse> getListBookingsToday(int pageNumber, int pageSize, Status status) {
        Pageable pageableRequest = PageRequest.of(pageNumber, pageSize);
        Page<Booking> pageable = bookingRepository.findAllBookingByGivenAtAndStatus(
                LocalDate.now(), status, pageableRequest);

        return getBookingResponseList(pageable.getContent());
    }

    @Override
    public boolean hasBookingForUser(Long userId) {
        return bookingRepository.existsBookingByStudent_Id(userId);
    }

    private List<BookingResponse> getBookingResponseList(List<Booking> bookingList) {
        return bookingList.stream()
                .map(b -> new BookingResponse(b.getId(), b.getStudent().getName(), b.getStudent().getSurname()))
                .toList();
    }
}
