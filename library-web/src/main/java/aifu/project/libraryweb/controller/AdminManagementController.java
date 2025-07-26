package aifu.project.libraryweb.controller;

import aifu.project.common_domain.dto.AdminCreateRequest;
import aifu.project.common_domain.dto.AdminResponse;
import aifu.project.common_domain.payload.ResponseMessage;
import aifu.project.libraryweb.service.AdminManagementService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/superadmin/admins")
@RequiredArgsConstructor
public class AdminManagementController {

    private final AdminManagementService adminManagementService;

    // In a real app with Spring Security, you would add: @PreAuthorize("hasRole('LIBRARIAN')")
    @PostMapping
    public ResponseEntity<ResponseMessage> createAdmin(@Valid @RequestBody AdminCreateRequest request) {
        // Thanks to GlobalExceptionHandler, no try-catch is needed here.
        AdminResponse responseDto = adminManagementService.createAdmin(request);
        ResponseMessage response = new ResponseMessage(true, "Admin created successfully.", responseDto);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }
}