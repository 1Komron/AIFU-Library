package aifu.project.libraryweb.controller.admin_controller;

import aifu.project.libraryweb.service.exel.BackupService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin/backup")
@RequiredArgsConstructor
public class BackupController {
    private final BackupService backupService;

    @GetMapping("/history")
    @Operation(summary = "Bronlar tarixini excel holatda yuklab olish")
    public ResponseEntity<byte[]> exportHistoryExcel() {
        return backupService.backupHistory();
    }

    @GetMapping("/book")
    @Operation(summary = "Kitoblar ro'yxatini excel holatda yuklab olish")
    public ResponseEntity<byte[]> exportBookExcel() {
        return backupService.backupBook();
    }

    @GetMapping("/booking")
    @Operation(summary = "Bronlarni excel holatda yuklab olish")
    public ResponseEntity<byte[]> exportBookingExcel() {
        return backupService.backupBooking();
    }

    @GetMapping("/booking/student/{id}")
    @Operation(summary = "Bronlarni excel holatda yuklab olish (Student bo'yich)")
    public ResponseEntity<byte[]> exportBookingExcelByStudent(@PathVariable Long id) {
        return backupService.backupBooking(id);
    }

    @GetMapping("/student")
    @Operation(summary = "Studentlar ro'yxatini excel holatda yuklab olish")
    public ResponseEntity<byte[]> exportBackupStudents() {
        return backupService.backupStudents();
    }

}
