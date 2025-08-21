package aifu.project.libraryweb.service.exel;

import aifu.project.common_domain.dto.excel_dto.BookExcelDTO;
import aifu.project.common_domain.entity.Booking;
import aifu.project.common_domain.entity.History;
import aifu.project.common_domain.exceptions.UserNotFoundException;
import aifu.project.libraryweb.service.base_book_service.BaseBookService;
import aifu.project.libraryweb.service.booking_serivce.BookingService;
import aifu.project.libraryweb.service.exel.ExcelBackupExporter;
import aifu.project.libraryweb.service.history_service.HistoryService;
import aifu.project.libraryweb.service.student_service.StudentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class BackupService {
    @Value("${backup.path}")
    private String backupPath;
    private final HistoryService historyService;
    private final BaseBookService baseBookService;
    private final BookingService bookingService;
    private final StudentService studentService;


    public void backupHistory() {
        List<History> historyList = historyService.getAll();
        ExcelBackupExporter.exportHistoryExcel(historyList, backupPath);
    }

    public void backupBook() {
        List<BookExcelDTO> booksList = baseBookService.getAllBooks();
        ExcelBackupExporter.exportBookExcel(booksList, backupPath);
    }

    public void backupBooking() {
        List<Booking> bookings = bookingService.getAllBookings();
        ExcelBackupExporter.exportBookingExcel(bookings, backupPath, false);
    }

    public void backupBooking(Long id) {
        if (!studentService.existsStudent(id)) {
            throw new UserNotFoundException("Student topilmadi. ID: " + id);
        }

        List<Booking> bookings = bookingService.getAllBookingsByStudent(id);
        ExcelBackupExporter.exportBookingExcel(bookings, backupPath, true);
    }
}
