package aifu.project.libraryweb.service;

import aifu.project.libraryweb.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;

    public long countUsers() {
        return userRepository.getUsersCount();
    }
}
