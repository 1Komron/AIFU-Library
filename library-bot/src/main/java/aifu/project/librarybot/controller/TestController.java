package aifu.project.librarybot.controller;

import aifu.project.librarybot.exel.ExcelBackupExporter;
import aifu.project.librarybot.service.HistoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class TestController {
    private final HistoryService historyService;

    @GetMapping("/test")
    public String test() {
        ExcelBackupExporter.exportHistoryExcel(historyService.getHistoryList(),"/home/komronbek");
        return "test";
    }
}
