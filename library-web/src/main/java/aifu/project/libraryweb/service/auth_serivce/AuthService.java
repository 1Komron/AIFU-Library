package aifu.project.libraryweb.service.auth_serivce;

import aifu.project.common_domain.dto.ResponseMessage;
import aifu.project.common_domain.dto.auth_dto.LoginDTO;
import aifu.project.common_domain.dto.auth_dto.SignUpDTO;
import org.springframework.http.ResponseEntity;

public interface AuthService {
    ResponseEntity<ResponseMessage> login(LoginDTO loginDTO);

    ResponseEntity<ResponseMessage> signUp(SignUpDTO signUpDTO);

    ResponseEntity<ResponseMessage> getMe();
}
