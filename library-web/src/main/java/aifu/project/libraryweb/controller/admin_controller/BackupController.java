package aifu.project.libraryweb.controller.admin_controller;

import aifu.project.libraryweb.service.exel.BackupService;
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
    public ResponseEntity<byte[]> exportHistoryExcel() {
        return backupService.backupHistory();
    }

    @GetMapping("/book")
    public ResponseEntity<byte[]> exportBookExcel() {
        return backupService.backupBook();
    }

    @GetMapping("/booking")
    public ResponseEntity<byte[]> exportBookingExcel() {
        return backupService.backupBooking();
    }

    @GetMapping("/booking/student/{id}")
    public ResponseEntity<byte[]> exportBookingExcelByStudent(@PathVariable Long id) {
        return backupService.backupBooking(id);
    }

    @GetMapping("/student")
    public ResponseEntity<byte[]> exportBackupStudents() {
        return backupService.backupStudents();
    }

}
