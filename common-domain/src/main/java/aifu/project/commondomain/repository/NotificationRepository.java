package aifu.project.commondomain.repository;

import aifu.project.commondomain.entity.Notification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface NotificationRepository extends JpaRepository<Notification, Integer> {
    Notification getNotificationById(Long id);

    Page<Notification> findNotificationByIsRead(boolean read, Pageable pageable);
}
