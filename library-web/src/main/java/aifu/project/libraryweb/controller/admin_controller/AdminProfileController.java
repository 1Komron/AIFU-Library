package aifu.project.libraryweb.controller.admin_controller;

import aifu.project.common_domain.dto.ResponseMessage;
import aifu.project.libraryweb.service.librarian_service.LibrarianService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/admin/profile")
@RequiredArgsConstructor
public class AdminProfileController {
    private final LibrarianService librarianService;

    @GetMapping
    public ResponseEntity<ResponseMessage> getAdminProfile() {
        return librarianService.profile();
    }

    @PatchMapping
    public ResponseEntity<ResponseMessage> updateAdminProfile(@RequestBody Map<String, Object> updates) {
        return librarianService.update(updates);
    }
}
