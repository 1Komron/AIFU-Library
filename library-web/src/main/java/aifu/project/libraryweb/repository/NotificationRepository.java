package aifu.project.libraryweb.repository;

import aifu.project.common_domain.entity.Notification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;


@Repository
public interface NotificationRepository extends JpaRepository<Notification, Integer> {
    Page<Notification> findNotificationByIsRead(boolean read, Pageable pageable);

    Optional<Notification> findNotificationById(Long id);
}
