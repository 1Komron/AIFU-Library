package aifu.project.librarybot.repository;

import aifu.project.commondomain.entity.Notification;
import aifu.project.commondomain.entity.enums.RequestType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;


@Repository
public interface NotificationRepository extends JpaRepository<Notification, Integer> {
    Notification getNotificationById(Long id);

    Page<Notification> findNotificationByIsRead(boolean read, Pageable pageable);

    @Query("select n.id from Notification n where n.requestId = :requestId and n.requestType = :requestType")
    Long findNotificationIdByRequestIdAndRequestType(Long requestId, RequestType requestType);

    Optional<Notification> findNotificationById(Long id);
}
