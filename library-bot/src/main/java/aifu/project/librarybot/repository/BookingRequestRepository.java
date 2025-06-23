package aifu.project.librarybot.repository;

import aifu.project.common_domain.entity.BookCopy;
import aifu.project.common_domain.entity.BookingRequest;
import aifu.project.common_domain.entity.enums.BookingRequestStatus;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface BookingRequestRepository extends JpaRepository<BookingRequest, Long> {
    BookingRequest findBookingRequestByUserChatIdAndBookCopyIdAndStatus(Long chatId, Integer bookCopyId, BookingRequestStatus status);

    Optional<BookingRequest> findBookingRequestById(Long id);

    boolean existsBookingRequestByUser_Id(Long userId);

    boolean existsBookingRequestByUser_ChatId(Long userChatId);

    boolean existsByBookCopy(BookCopy bookCopy);
}
