package aifu.project.librarybot.runner;

import aifu.project.librarybot.scheduler.OverdueNotificationScheduler;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class Runner implements CommandLineRunner {
    private  final OverdueNotificationScheduler scheduler;
    private final RabbitAdmin rabbitAdmin;

    @Override
    public void run(String... args) throws Exception {
        scheduler.sendOverdueExpiredNotifications();
        scheduler.sendOverdueExpiringNotifications();
        scheduler.changBookingStatus();
        rabbitAdmin.initialize();
    }
}
