package aifu.project.libraryweb.controller.admin_controller;


import aifu.project.common_domain.dto.auth_dto.LoginDTO;
import aifu.project.common_domain.dto.auth_dto.SignUpDTO;
import aifu.project.common_domain.dto.ResponseMessage;
import aifu.project.libraryweb.service.auth_serivce.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;

    @PostMapping("/login")
    @Operation(summary = "Adminlar login qilish")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Login muvaffaqiyatli"),
            @ApiResponse(responseCode = "401", description = "email | password noto'g'ri"),
    })

    public ResponseEntity<ResponseMessage> login(@RequestBody LoginDTO loginDTO) {
        return authService.login(loginDTO);
    }

    @PostMapping("/sing-up")
    @Operation(summary = "Adminlar ro'yxatdan o'tishi")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Ro'yxatdan o'tish muvaffaqiyatli"),
            @ApiResponse(responseCode = "400", description = "Bad Request - email allaqachon mavjud"),
    })
    public ResponseEntity<ResponseMessage> signUp(@RequestBody SignUpDTO signUpDTO) {
        return authService.signUp(signUpDTO);
    }

    @PostMapping("/me")
    @Operation(summary = "Sistemadagi admin haqida ma'lumot olish")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Muvaffaqiyatli"),
            @ApiResponse(responseCode = "401", description = "Login qilinmagan"),
    })
    public ResponseEntity<ResponseMessage> getCurrentUser() {
        return authService.getMe();
    }
}
