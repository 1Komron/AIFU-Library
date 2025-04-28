package aifu.project.librarybot.scheduler;

import aifu.project.commondomain.entity.User;
import aifu.project.commondomain.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class ReRegistrationScheduler {
    private final UserRepository userRepository;

    @Scheduled(cron = "0 0 0 1 9 *")
    public void reRegistration() {
        List<User> all = userRepository.findAll();
        all.forEach(user -> user.setActive(false));
        userRepository.saveAll(all);
    }
}
