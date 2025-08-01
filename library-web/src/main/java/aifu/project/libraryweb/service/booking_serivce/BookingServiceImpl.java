package aifu.project.libraryweb.service.booking_serivce;

import aifu.project.common_domain.dto.booking_dto.*;
import aifu.project.common_domain.dto.statistic_dto.BookingDiagramDTO;
import aifu.project.common_domain.entity.BookCopy;
import aifu.project.common_domain.entity.Booking;
import aifu.project.common_domain.entity.Student;
import aifu.project.common_domain.entity.enums.Status;
import aifu.project.common_domain.exceptions.BookingNotFoundException;
import aifu.project.common_domain.dto.ResponseMessage;
import aifu.project.libraryweb.repository.BookingRepository;
import aifu.project.libraryweb.service.student_service.StudentServiceImpl;
import aifu.project.libraryweb.service.base_book_service.BookCopyService;
import aifu.project.libraryweb.service.history_service.HistoryService;
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
    private StudentServiceImpl studentService;
    private final BookCopyService bookCopyService;
    private final HistoryService historyService;

    private static final String CURRENT_PAGE = "currentPage";
    private static final String TOTAL_PAGES = "totalPages";
    private static final String TOTAL_ELEMENTS = "totalElements";

    @Override
    public ResponseEntity<ResponseMessage> getBookingList(int pageNum, int pageSize) {
        Pageable pageable = PageRequest.of(--pageNum, pageSize);
        Page<BookingShortDTO> page = bookingRepository.findAllBookingShortDTO(pageable);

        Map<String, Object> data = Map.of(
                "data", page.getContent(),
                CURRENT_PAGE, page.getNumber() + 1,
                TOTAL_PAGES, page.getTotalPages(),
                TOTAL_ELEMENTS, page.getTotalElements()
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
                CURRENT_PAGE, page.getNumber() + 1,
                TOTAL_PAGES, page.getTotalPages(),
                TOTAL_ELEMENTS, page.getTotalElements()
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
    public ResponseEntity<ResponseMessage> extendBooking(ExtendBookingDTO request) {
        Long id = request.bookingId();
        Integer extendDays = request.extendDays();

        Booking booking = bookingRepository.findById(id)
                .orElseThrow(() -> new BookingNotFoundException("Booking not found with id: " + id));

        extendDueDate(booking, extendDays);

        return ResponseEntity.ok(new ResponseMessage(true, "Booking extended successfully", null));
    }

    private void extendDueDate(Booking booking, Integer extendDays) {
        LocalDate dueDate = booking.getDueDate();
        LocalDate now = LocalDate.now();

        LocalDate newDueDate = dueDate.isAfter(now) ? dueDate.plusDays(extendDays) : now.plusDays(extendDays);

        booking.setStatus(Status.APPROVED);
        booking.setDueDate(newDueDate);

        bookingRepository.save(booking);
    }

    //Search ni qoshib qoyish kerak
    @Override
    public ResponseEntity<ResponseMessage> getBookingByStudentId(Long id, int pageNum, int pageSize) {
//        Pageable pageable = PageRequest.of(--pageNum, pageSize);
//        Page<Booking> page = bookingRepository.findByStudent_IdAndIsDeletedFalse(id, pageable);
//
//        Map<String, Object> data = Map.of(
//                "data", getBookingResponseList(page.getContent()),
//                CURRENT_PAGE, page.getNumber() + 1,
//                TOTAL_PAGES, page.getTotalPages(),
//                TOTAL_ELEMENTS, page.getTotalElements()
//        );

        return ResponseEntity.ok(new ResponseMessage(true, "Bookings for student with id: " + id, null));
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

    @Override
    public void setStudentService(StudentServiceImpl studentService) {
        this.studentService = studentService;
    }
}
