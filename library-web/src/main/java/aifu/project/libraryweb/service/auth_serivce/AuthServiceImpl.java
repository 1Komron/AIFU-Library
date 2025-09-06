package aifu.project.libraryweb.service.auth_serivce;

import aifu.project.common_domain.dto.AdminResponse;
import aifu.project.common_domain.dto.auth_dto.LoginDTO;
import aifu.project.common_domain.entity.Librarian;
import aifu.project.common_domain.exceptions.LoginBadCredentialsException;
import aifu.project.common_domain.dto.ResponseMessage;
import aifu.project.libraryweb.entity.SecurityLibrarian;
import aifu.project.libraryweb.repository.LibrarianRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {
    private final LibrarianRepository librarianRepository;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;

    @Override
    public ResponseEntity<ResponseMessage> login(LoginDTO loginDTO) {
        String email = loginDTO.email();
        String password = loginDTO.password();

        log.info("Login attempt: email={}", email);

        Librarian librarian = librarianRepository.findByEmailAndIsDeletedFalse(email)
                .orElseThrow(() -> new LoginBadCredentialsException("Invalid email or password"));

        if (!librarian.isActive()) {
            throw new LoginBadCredentialsException("Account is not active");
        }

        if (!passwordEncoder.matches(password, librarian.getPassword())) {
            throw new LoginBadCredentialsException("Invalid email or password");
        }

        AdminResponse response = AdminResponse.builder()
                .id(librarian.getId())
                .name(librarian.getName())
                .surname(librarian.getSurname())
                .email(librarian.getEmail())
                .role(librarian.getRole().name())
                .imageUrl(librarian.getImageUrl())
                .isActive(librarian.isActive())
                .build();

        String token = jwtService.generateToken(email);

        return ResponseEntity.ok(new ResponseMessage(true, "Login successful",
                Map.of("token", token, "user", response)));
    }

    @Override
    public ResponseEntity<ResponseMessage> getMe() {
        SecurityLibrarian securityLibrarian = (SecurityLibrarian) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Librarian librarian = securityLibrarian.toBase();

        if (librarian == null) {
            throw new LoginBadCredentialsException("No user is logged in");
        }

        AdminResponse response = AdminResponse.builder()
                .id(librarian.getId())
                .name(librarian.getName())
                .surname(librarian.getSurname())
                .email(librarian.getEmail())
                .role(librarian.getRole().name())
                .imageUrl(librarian.getImageUrl())
                .isActive(librarian.isActive())
                .build();

        return ResponseEntity.ok(new ResponseMessage(true, "Data", response));
    }
}
