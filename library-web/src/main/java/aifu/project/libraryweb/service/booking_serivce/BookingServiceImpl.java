package aifu.project.libraryweb.service.booking_serivce;

import aifu.project.common_domain.dto.booking_dto.*;
import aifu.project.common_domain.dto.statistic_dto.BookingDiagramDTO;
import aifu.project.common_domain.entity.BookCopy;
import aifu.project.common_domain.entity.Booking;
import aifu.project.common_domain.entity.Librarian;
import aifu.project.common_domain.entity.Student;
import aifu.project.common_domain.entity.enums.Status;
import aifu.project.common_domain.exceptions.BookCopyIsTakenException;
import aifu.project.common_domain.exceptions.BookingNotFoundException;
import aifu.project.common_domain.dto.ResponseMessage;
import aifu.project.libraryweb.entity.SecurityLibrarian;
import aifu.project.libraryweb.repository.BookingRepository;
import aifu.project.libraryweb.service.student_service.StudentServiceImpl;
import aifu.project.libraryweb.service.base_book_service.BookCopyService;
import aifu.project.libraryweb.service.history_service.HistoryService;
import aifu.project.libraryweb.utils.Util;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {
    private final BookingRepository bookingRepository;
    private StudentServiceImpl studentService;
    private final BookCopyService bookCopyService;
    private final HistoryService historyService;

    private static final String DEFAULT = "default";

    @Override
    public ResponseEntity<ResponseMessage> getBooking(Long id) {
        BookingSummaryDTO data = bookingRepository.findSummary(id)
                .orElseThrow(() -> new BookingNotFoundException("Booking not found. By id: " + id));

        return ResponseEntity.ok(new ResponseMessage(true, "Booking by id: " + id, data));
    }

    @Override
    public ResponseEntity<ResponseMessage> getAll(String field, String query, String filter, int pageNum, int pageSize, String sortDirection) {
        field = field == null ? DEFAULT : field;
        if (!field.equals(DEFAULT) && query == null) {
            throw new IllegalArgumentException("Query qiymati: null. Field qiymati: %s".formatted(filter));
        }

        Sort.Direction direction = sortDirection.equalsIgnoreCase("asc") ? Sort.Direction.ASC : Sort.Direction.DESC;
        Pageable pageable = PageRequest.of(--pageNum, pageSize, Sort.by(direction, "id"));

        List<Status> statuses = switch (filter.toUpperCase()) {
            case "APPROVED" -> List.of(Status.APPROVED);
            case "OVERDUE" -> List.of(Status.OVERDUE);
            default -> List.of(Status.APPROVED, Status.OVERDUE);
        };

        Page<BookingShortDTO> page = switch (field) {
            case "studentId" ->
                    bookingRepository.findAllBookingShortDTOByStudentId(Long.parseLong(query), statuses, pageable);
            case "cardNumber" -> bookingRepository.findAllBookingShortDTOByStudentCardNumber(query, statuses, pageable);
            case "fullName" -> {
                String[] parts = query.trim().split("\\s+");

                String first = "%" + parts[0].toLowerCase() + "%";
                String second = (parts.length == 2) ? "%" + parts[1].toLowerCase() + "%" : null;

                yield bookingRepository.findAllBookingShortDTOByStudentFullName(first, second, statuses, pageable);
            }
            case "bookEpc" -> bookingRepository.findAllBookingShortDTOByBookEpc(query, statuses, pageable);
            case "inventoryNumber" ->
                    bookingRepository.findAllBookingShortDTOByBookInventoryNumber(query, statuses, pageable);
            case DEFAULT -> bookingRepository.findAllBookingShortDTO(pageable, statuses);
            default -> throw new IllegalArgumentException("Mavjud bo'lmagan field: " + field);
        };

        List<BookingShortDTO> content = page.getContent();
        log.info("Bookinglar ro'yxati: field={}, query={}, filter={}, pageNum={}, pageSize={}, sortDirection={}",
                field, query, filter, pageNum, pageSize, sortDirection);
        log.info("Bookinglar ro'yxati: {}", content.stream().map(BookingShortDTO::id).toList());

        Map<String, Object> map = Util.getPageInfo(page);
        map.put("data", content);

        return ResponseEntity.ok(new ResponseMessage(true, "data", map));
    }

    @Override
    @Transactional
    public ResponseEntity<ResponseMessage> borrowBook(BorrowBookDTO request) {
        String cardNumber = request.cardNumber();
        String epc = request.epc();
        Integer days = request.days();

        Student student = studentService.findByCardNumber(cardNumber);

//        BookCopy bookCopy = bookCopyService.findByEpc(epc);
        BookCopy bookCopy = bookCopyService.findByInventoryNumber(epc);

        if (bookCopy.isTaken()) {
            log.error("Booking qilingan book copy: {}", bookCopy.getId());
            throw new BookCopyIsTakenException("Book copy allaqachon booking qilingan: " + bookCopy.getId());
        }

        createBooking(student, bookCopy, days);

        bookCopyService.updateStatus(bookCopy, true);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ResponseMessage(true, "Booking muvaffaqiyatli yaratildi", null));
    }

    @Override
    @Transactional
    public ResponseEntity<ResponseMessage> returnBook(ReturnBookDTO request) {
        String cardNumber = request.cardNumber();
        String epc = request.epc();

        Student student = studentService.findByCardNumber(cardNumber);

//        BookCopy bookCopy = bookCopyService.findByEpc(epc);
        BookCopy bookCopy = bookCopyService.findByInventoryNumber(epc);

        Booking booking = bookingRepository.findByStudentAndBook(student, bookCopy)
                .orElseThrow(() -> new BookingNotFoundException("Bunday booking mavjud emas. Student: %s va book copy: %s"
                        .formatted(student.getId(), bookCopy.getId())));

        bookCopyService.updateStatus(bookCopy, false);

        historyService.add(booking);

        bookingRepository.delete(booking);
        log.info("Booking muvaffaqiyatli qaytarildi. Booking: {}", booking);
        log.info("Booking ochirildi va tarixga qo'shildi");

        return ResponseEntity.ok(new ResponseMessage(true, "Kitob muvaffaqiyatli qaytib olindi", null));
    }

    public void createBooking(Student student, BookCopy bookCopy, Integer days) {
        SecurityLibrarian securityLibrarian = (SecurityLibrarian) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Librarian librarian = securityLibrarian.toBase();
        Booking booking = new Booking();
        booking.setIssuedBy(librarian);
        booking.setStudent(student);

        booking.setBook(bookCopy);

        LocalDate now = LocalDate.now();
        booking.setGivenAt(now);
        booking.setDueDate(LocalDate.now().plusDays(days));

        Booking save = bookingRepository.save(booking);

        log.info("Booking muvaffaqiyatli yaratildi. Booking: {}", save);
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
        if (extendDays == null || extendDays < 1) {
            throw new IllegalArgumentException("Noto'g'ri uzaytirish kunlari kiritildi: " + extendDays);
        }
        SecurityLibrarian securityLibrarian = (SecurityLibrarian) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Librarian librarian = securityLibrarian.toBase();

        LocalDate dueDate = booking.getDueDate();
        LocalDate now = LocalDate.now();

        LocalDate newDueDate = dueDate.isAfter(now) ? dueDate.plusDays(extendDays) : now.plusDays(extendDays);

        booking.setStatus(Status.APPROVED);
        booking.setDueDate(newDueDate);

        booking.setExtendedBy(librarian);
        booking.setExtendedAt(LocalDate.now());

        log.info("Booking uzaytirildi. Booking ID: {}, yangi muddati: {}", booking.getId(), newDueDate);

        bookingRepository.save(booking);
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
