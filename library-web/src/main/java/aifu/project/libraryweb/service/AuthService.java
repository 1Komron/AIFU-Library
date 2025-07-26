package aifu.project.libraryweb.service;

import aifu.project.common_domain.dto.auth_dto.LoginDTO;
import aifu.project.common_domain.dto.auth_dto.SignUpDTO;
import aifu.project.common_domain.entity.Librarian;
import aifu.project.common_domain.exceptions.LoginBadCredentialsException;
import aifu.project.common_domain.payload.ResponseMessage;
import aifu.project.libraryweb.repository.LibrarianRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {
    private final LibrarianRepository librarianRepository;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;

    public ResponseEntity<ResponseMessage> login(LoginDTO loginDTO) {
        String email = loginDTO.email();
        String password = loginDTO.password();

        log.info("Login attempt: email={}", email);

        Librarian librarian = librarianRepository.findByEmailAndIsDeletedFalse(email)
                .orElseThrow(() -> new LoginBadCredentialsException("Invalid email or password"));

        if (!passwordEncoder.matches(password, librarian.getPassword())) {
            throw new LoginBadCredentialsException("Invalid email or password");
        }

        String token = jwtService.generateToken(email);
        return ResponseEntity.ok(new ResponseMessage(true, "Login successful", token));
    }

    public ResponseEntity<ResponseMessage> signUp(SignUpDTO signUpDTO) {
        return ResponseEntity.ok(new ResponseMessage(true, "Sign up successful", signUpDTO));
    }
}
