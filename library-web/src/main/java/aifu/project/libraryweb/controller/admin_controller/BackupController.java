package aifu.project.libraryweb.controller.admin_controller;


import aifu.project.libraryweb.service.exel.BackupService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin/backup")
@RequiredArgsConstructor
public class BackupController {
    private final BackupService backupService;

    @GetMapping("/history")
    public void exportHistoryExcel() {
        backupService.backupHistory();
    }

    @GetMapping("/book")
    public void exportBookExcel() {
        backupService.backupBook();
    }

}
