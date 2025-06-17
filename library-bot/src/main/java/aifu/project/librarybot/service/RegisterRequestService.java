package aifu.project.librarybot.service;

import aifu.project.common_domain.entity.User;
import aifu.project.common_domain.entity.RegisterRequest;

import aifu.project.librarybot.repository.RegisterRequestRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
public class RegisterRequestService {
    private final RegisterRequestRepository registerRequestRepository;

    public RegisterRequest create(User user) {
        RegisterRequest registerRequest = new RegisterRequest();
        registerRequest.setUser(user);

        return registerRequestRepository.save(registerRequest);
    }

    public RegisterRequest getRegisterRequest(String chatId) {
        return registerRequestRepository.findByUser_ChatId(Long.parseLong(chatId));
    }

    public void delete(RegisterRequest request) {
        registerRequestRepository.delete(request);
    }

    public boolean hasRequestForUser(Long userId) {
        return registerRequestRepository.existsRegisterRequestByUser_Id(userId);
    }
}
