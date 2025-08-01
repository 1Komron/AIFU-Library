/*package aifu.project.libraryweb.controller;

import aifu.project.common_domain.dto.ResponseMessage;
import aifu.project.libraryweb.config.ImportStats;
import aifu.project.libraryweb.service.StudentExcelImportService;
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
public class StudentImportController {

    private final StudentExcelImportService studnetexcelImportService;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ResponseMessage> uploadUsers(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            // Xatolik javobini ham ResponseMessage formatida qaytaramiz
            ResponseMessage response = new ResponseMessage(false, "Fayl bo'sh bo'lishi mumkin emas.", null);
            return ResponseEntity.badRequest().body(response);
        }

        try {
            // 1. Servisdan sof statistika obyektini olamiz
            ImportStats stats = studnetexcelImportService.importStudentsFromExcel(file.getInputStream());

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
}*/
package aifu.project.libraryweb.controller.super_admin_controller; // Sizning paket nomingiz

import aifu.project.common_domain.dto.ResponseMessage;
import aifu.project.libraryweb.config.ImportStats;
import aifu.project.libraryweb.service.StudentExcelImportService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/super-admin/students/import") // Manzilni 'students' ga o'zgartirganimiz ma'qul
@RequiredArgsConstructor
public class StudentImportController {

    private final StudentExcelImportService studentExcelImportService;

    /**
     * Foydalanuvchilarni import qilish uchun standart Excel shablonini yuklab olish.
     * Bu metod loyihaning 'resources/templates' papkasidagi faylni topib, foydalanuvchiga yuklab beradi.
     */
    @Operation(summary = "Import uchun Excel shablonini yuklab olish")
    @GetMapping("/template")
    public ResponseEntity<Resource> downloadTemplate() {
        try {
            // ClassPathResource - bu loyihaning 'classpath'idan (ya'ni, resources papkasidan)
            // fayllarni topish uchun Spring'ning eng ishonchli vositasi.
            Resource resource = new ClassPathResource("templates/student_import_template.xlsx");

            if (resource.exists()) {
                String filename = "student_import_template.xlsx";

                // Maxsus HTTP sarlavhalarini (Headers) yaratamiz.
                // "Content-Disposition: attachment" brauzerga bu faylni ko'rsatishga harakat qilmasdan,
                // to'g'ridan-to'g'ri yuklab olish kerakligini aytadi.
                HttpHeaders headers = new HttpHeaders();
                headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + filename);

                // Muvaffaqiyatli javobni faylning o'zi va sarlavhalar bilan birga qaytaramiz.
                return ResponseEntity.ok()
                        .headers(headers)
                        .contentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                        .body(resource);
            } else {
                // Agar biror sabab bilan fayl topilmasa, 404 Not Found xatoligini qaytaramiz.
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            // Kutilmagan xatolik bo'lsa, 500 Internal Server Error xatoligini qaytaramiz.
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * To'ldirilgan shablon faylini qabul qilib, import jarayonini boshlash.
     */
    @Operation(summary = "To'ldirilgan Excel shablonini yuklash")
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ResponseMessage> uploadStudents(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            ResponseMessage response = new ResponseMessage(false, "Fayl bo'sh bo'lishi mumkin emas.", null);
            return ResponseEntity.badRequest().body(response);
        }

        try {
            ImportStats stats = studentExcelImportService.importStudentsFromExcel(file.getInputStream());
            ResponseMessage response = new ResponseMessage(true, "Import jarayoni yakunlandi.", stats);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            ResponseMessage response = new ResponseMessage(false, "Xatolik: " + e.getMessage(), null);
            return ResponseEntity.badRequest().body(response);
        } catch (Exception e) {
            ResponseMessage response = new ResponseMessage(false, "Tizimda kutilmagan ichki xatolik yuz berdi.", null);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
}