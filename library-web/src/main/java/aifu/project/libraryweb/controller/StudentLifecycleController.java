package aifu.project.libraryweb.controller;

import aifu.project.common_domain.dto.ResponseMessage;
import aifu.project.common_domain.dto.action_dto.DeactivationStats;
import aifu.project.libraryweb.service.StudentDeactivationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/super-admin/students/lifecycle")
@RequiredArgsConstructor
public class StudentLifecycleController {

    private final StudentDeactivationService deactivationService;

    @PostMapping(value = "/deactivate-graduates", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasRole('SUPER_ADMIN')") // Yoki SUPER_ADMIN
    public ResponseEntity<ResponseMessage> deactivateGraduates(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            return ResponseEntity.badRequest()
                    .body(new ResponseMessage(false, "Fayl bo'sh bo'lishi mumkin emas.", null));
        }

        try {
            DeactivationStats stats = deactivationService.deactivateStudentsFromExcel(file.getInputStream());
            return ResponseEntity.ok(new ResponseMessage(true, "Deaktivatsiya jarayoni yakunlandi.", stats));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ResponseMessage(false, "Jarayon davomida kutilmagan xatolik yuz berdi.", null));
        }
    }
}