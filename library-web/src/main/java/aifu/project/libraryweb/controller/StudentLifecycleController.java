package aifu.project.libraryweb.controller;

import aifu.project.common_domain.dto.ResponseMessage;
import aifu.project.common_domain.dto.action_dto.DeactivationStats;
import aifu.project.libraryweb.service.StudentDeactivationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;

@RestController
@RequestMapping("/api/super-admin/students/lifecycle")
@RequiredArgsConstructor
public class StudentLifecycleController {

    private final StudentDeactivationService deactivationService;

     @Operation ( summary = "Studentlarni o'chiradi qilish",
             description = "Excel faylidagi studentlarni bazadan  o'chiradi" )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Deaktivatsiya jarayoni muvaffaqiyatli yakunlandi"),
            @ApiResponse(responseCode = "400", description = "Fayl yuborilishi shart"),
            @ApiResponse(responseCode = "500", description = "Serverdagi ichki xatolik")
    })

    @PostMapping(value = "/deactivate-graduates", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public ResponseEntity<ResponseMessage> deactivateGraduates(@RequestParam("file") MultipartFile file) throws IOException {
        if (file == null || file.isEmpty()) {
            return ResponseEntity.badRequest()
                    .body(new ResponseMessage(false, "Fayl yuborilishi shart.", null));
        }
        DeactivationStats stats = deactivationService.deactivateStudents(file.getInputStream());
        return ResponseEntity.ok(new ResponseMessage(true, "Deaktivatsiya jarayoni muvaffaqiyatli yakunlandi.", stats));
    }
}