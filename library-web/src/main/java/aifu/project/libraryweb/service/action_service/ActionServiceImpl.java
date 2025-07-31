package aifu.project.libraryweb.service.action_service;

import aifu.project.common_domain.dto.ResponseMessage;
import aifu.project.common_domain.dto.action_dto.ExtendAcceptActionDTO;
import aifu.project.common_domain.dto.action_dto.ExtendRejectActionDTO;
import aifu.project.common_domain.dto.action_dto.WarningActionDTO;
import aifu.project.common_domain.entity.Booking;
import aifu.project.common_domain.entity.Notification;
import aifu.project.common_domain.entity.enums.Status;
import aifu.project.common_domain.exceptions.BookingNotFoundException;
import aifu.project.common_domain.exceptions.NotificationNotFoundException;
import aifu.project.libraryweb.repository.BookingRepository;
import aifu.project.libraryweb.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

import static aifu.project.common_domain.exceptions.NotificationNotFoundException.NOTIFICATION_NOT_FOUND;

@Slf4j
@Service
@RequiredArgsConstructor
public class ActionServiceImpl implements ActionService {
    private final NotificationRepository notificationRepository;
    private final BookingRepository bookingRepository;

    @Override
    public ResponseEntity<ResponseMessage> extendReject(ExtendRejectActionDTO extendRejectActionDTO) {
        Long notificationId = extendRejectActionDTO.notificationId();

        Notification notification = notificationRepository.findNotificationById(notificationId)
                .orElseThrow(() -> new NotificationNotFoundException(NOTIFICATION_NOT_FOUND + notificationId));

        log.info("Extend reject by notificationId:{}", notificationId);

        notificationRepository.delete(notification);

        return ResponseEntity.ok(new ResponseMessage(true, "Booking extend rejected successfully", null));
    }

    @Override
    public ResponseEntity<ResponseMessage> extendAccept(ExtendAcceptActionDTO extendAcceptActionDTO) {
        Long notificationId = extendAcceptActionDTO.notificationId();
        Integer extendDays = extendAcceptActionDTO.extendDays();

        Notification notification = notificationRepository.findNotificationById(notificationId)
                .orElseThrow(() -> new NotificationNotFoundException(NOTIFICATION_NOT_FOUND + notificationId));

        Booking booking = bookingRepository.findByStudentAndBook(notification.getStudent(), notification.getBookCopy())
                .orElseThrow(() -> new BookingNotFoundException("Booking not found by id: " + notificationId));

        extendDeadline(booking, extendDays);

        notificationRepository.delete(notification);

        log.info("Extend accept by bookingID:{} to {} days", booking.getId(), extendDays);

        return ResponseEntity.ok(new ResponseMessage(true, "Booking extended successfully", null));
    }

    @Override
    public ResponseEntity<ResponseMessage> warning(WarningActionDTO warningActionDTO) {
        Long notificationId = warningActionDTO.notificationId();
        Notification notification = notificationRepository.findNotificationById(notificationId)
                .orElseThrow(() -> new NotificationNotFoundException(NOTIFICATION_NOT_FOUND + notificationId));

        notificationRepository.delete(notification);

        log.info("Deleted warning notification by notificationId:{}", notificationId);

        return ResponseEntity.ok(new ResponseMessage(true, "Notification deleted successfully", null));
    }

    private void extendDeadline(Booking booking, Integer extendDays) {
        LocalDate dueDate = booking.getDueDate();
        LocalDate now = LocalDate.now();

        LocalDate newDueDate = dueDate.isAfter(now) ? dueDate.plusDays(extendDays) : now.plusDays(extendDays);

        booking.setDueDate(newDueDate);
        booking.setStatus(Status.APPROVED);

        bookingRepository.save(booking);
    }
}
