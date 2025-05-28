package aifu.project.librarybot.service;

import aifu.project.commondomain.entity.User;
import aifu.project.commondomain.entity.RegisterRequest;
import aifu.project.commondomain.repository.RegisterRequestRepository;
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
}
