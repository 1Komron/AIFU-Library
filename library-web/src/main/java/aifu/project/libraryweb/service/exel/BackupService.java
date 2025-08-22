package aifu.project.libraryweb.service.exel;

import aifu.project.common_domain.dto.ResponseMessage;
import aifu.project.common_domain.dto.excel_dto.BookExcelDTO;
import aifu.project.common_domain.entity.Booking;
import aifu.project.common_domain.entity.History;
import aifu.project.common_domain.entity.Student;

import aifu.project.libraryweb.service.base_book_service.BaseBookService;
import aifu.project.libraryweb.service.booking_serivce.BookingService;
import aifu.project.libraryweb.service.history_service.HistoryService;
import aifu.project.libraryweb.service.student_service.StudentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class BackupService {
    private final HistoryService historyService;
    private final BaseBookService baseBookService;
    private final BookingService bookingService;
    private final StudentService studentService;
    DateTimeFormatter format = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");


    public ResponseEntity<byte[]> backupHistory() {
        List<History> historyList = historyService.getAll();
        byte[] bytes = ExcelBackupExporter.exportHistoryExcel(historyList);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=Tarix(%s).xlsx"
                        .formatted(LocalDateTime.now().format(format)))
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(bytes);

    }

    public ResponseEntity<byte[]> backupBook() {
        List<BookExcelDTO> booksList = baseBookService.getAllBooks();
        byte[] bytes = ExcelBackupExporter.exportBookExcel(booksList);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=Kitoblar(%s).xlsx"
                        .formatted(LocalDateTime.now().format(format)))
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(bytes);
    }

    public ResponseEntity<byte[]> backupBooking() {
        List<Booking> bookings = bookingService.getAllBookings();
        byte[] bytes = ExcelBackupExporter.exportBookingExcel(bookings);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=Bron(%s).xlsx"
                        .formatted(LocalDateTime.now().format(format)))
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(bytes);
    }

    public ResponseEntity<byte[]> backupBooking(Long id) {
        Student student = studentService.findStudent(id);

        List<Booking> bookings = bookingService.getAllBookingsByStudent(id);
        byte[] bytes = ExcelBackupExporter.exportBookingExcel(bookings);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=Bron(%s %s).xlsx"
                        .formatted(student.getName(), student.getSurname()))
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(bytes);
    }

    public ResponseEntity<byte[]> backupStudents() {
        List<Student> students = studentService.getAll();
        byte[] bytes = ExcelBackupExporter.exportStudent(students);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=Student(%s).xlsx"
                        .formatted(LocalDateTime.now().format(format)))
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(bytes);
    }
}
