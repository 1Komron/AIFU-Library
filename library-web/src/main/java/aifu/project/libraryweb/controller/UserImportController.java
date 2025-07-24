package aifu.project.libraryweb.controller;

import aifu.project.common_domain.payload.ResponseMessage;
import aifu.project.libraryweb.config.ImportStats;
import aifu.project.libraryweb.service.ExcelImportService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

// controller paketingizda
@RestController
@RequestMapping("/api/v1/users/import")
@RequiredArgsConstructor
public class UserImportController {

    private final ExcelImportService excelImportService;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ResponseMessage> uploadUsers(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            // Xatolik javobini ham ResponseMessage formatida qaytaramiz
            ResponseMessage response = new ResponseMessage(false, "Fayl bo'sh bo'lishi mumkin emas.", null);
            return ResponseEntity.badRequest().body(response);
        }

        try {
            // 1. Servisdan sof statistika obyektini olamiz
            ImportStats stats = excelImportService.importUsersFromExcel(file.getInputStream());

            // 2. Muvaffaqiyatli javobni ResponseMessage formatida "o'raymiz"
            ResponseMessage response = new ResponseMessage(true, "Import jarayoni yakunlandi.", stats);
            return ResponseEntity.ok(response);

        } catch (IllegalArgumentException e) {
            // Tushunarli xatolikni ResponseMessage formatida "o'raymiz"
            ResponseMessage response = new ResponseMessage(false, "Xatolik: " + e.getMessage(), null);
            return ResponseEntity.badRequest().body(response);
        } catch (Exception e) {
            // Kutilmagan xatolikni ResponseMessage formatida "o'raymiz"
            ResponseMessage response = new ResponseMessage(false, "Tizimda kutilmagan ichki xatolik yuz berdi.", null);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
}