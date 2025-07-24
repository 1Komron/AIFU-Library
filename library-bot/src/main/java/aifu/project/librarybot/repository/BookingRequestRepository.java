package aifu.project.librarybot.repository;

import aifu.project.common_domain.entity.BookCopy;
import aifu.project.common_domain.entity.BookingRequest;

import org.springframework.data.jpa.repository.JpaRepository;


public interface BookingRequestRepository extends JpaRepository<BookingRequest, Long> {

    boolean existsBookingRequestByStudent_Id(Long userId);

    boolean existsByBookCopy(BookCopy bookCopy);
}
