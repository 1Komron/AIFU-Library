package aifu.project.libraryweb.controller.super_admin_controller;

import aifu.project.common_domain.dto.AccountActivationRequest;
import aifu.project.common_domain.dto.AdminCreateRequest;
import aifu.project.common_domain.dto.AdminResponse;
import aifu.project.common_domain.dto.ResponseMessage;
import aifu.project.libraryweb.service.AdminManagementService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/super-admin/admins")
@RequiredArgsConstructor
@Slf4j // Bu klassda ham log yozish foydali
public class AdminController {

    private final AdminManagementService adminManagementService;

    @PostMapping
    @Operation(summary = "Admin yaratish")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Admin muvaffaqiyatli yaratildi"),
            @ApiResponse(responseCode = "409", description = "Bu email bilan allaqachon royxatdan otilgan"),
    })
    public ResponseEntity<ResponseMessage> createAdmin(@Valid @RequestBody AdminCreateRequest request) {
        return adminManagementService.createAdmin(request);
    }

    @GetMapping
    public ResponseEntity<ResponseMessage> getAll(@RequestParam(required = false, defaultValue = "asc") String sortDirection) {
        return null;
    }


    @PostMapping("/activate")
    @PreAuthorize("hasRole('LIBRARIAN')")
    public ResponseEntity<ResponseMessage> activateAccount(@Valid @RequestBody AccountActivationRequest request) {
        log.info("HTTP Request: POST /api/admins/activate, email: {}", request.getEmail());
        adminManagementService.activateAccount(request);
        return ResponseEntity.ok(new ResponseMessage(true, "Akkaunt muvaffaqiyatli faollashtirildi!", null));
    }


}