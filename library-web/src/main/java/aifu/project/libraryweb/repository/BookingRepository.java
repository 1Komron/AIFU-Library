package aifu.project.libraryweb.repository;

import aifu.project.common_domain.dto.statistic_dto.BookingDiagramDTO;
import aifu.project.common_domain.dto.booking_dto.BookingShortDTO;
import aifu.project.common_domain.dto.booking_dto.BookingSummaryDTO;
import aifu.project.common_domain.entity.BookCopy;
import aifu.project.common_domain.entity.Booking;
import aifu.project.common_domain.entity.Student;
import aifu.project.common_domain.entity.enums.Status;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {
    @Query("""
            SELECT new aifu.project.common_domain.dto.booking_dto.BookingShortDTO(
                b.id,
                base.title,
                base.author,
                b.dueDate,
                b.givenAt,
                b.status
            )
            FROM Booking b
            JOIN b.book copy
            JOIN copy.book base
            """)
    Page<BookingShortDTO> findAllBookingShortDTO(Pageable pageable);

    @Query("""
            SELECT new aifu.project.common_domain.dto.booking_dto.BookingSummaryDTO(
                b.id,
                base.title,
                base.author,
                copy.inventoryNumber,
                b.dueDate,
                b.givenAt,
                b.status,
                s.id,
                s.name,
                s.surname
            )
            FROM Booking b
            JOIN b.book copy
            JOIN copy.book base
            JOIN b.student s
            WHERE b.id = :id
            """)
    Optional<BookingSummaryDTO> findSummary(@Param("id") Long id);

    Optional<Booking> findByStudentAndBook(Student student, BookCopy bookCopy);

    @Query("""
            SELECT new aifu.project.common_domain.dto.statistic_dto.BookingDiagramDTO(
                COUNT(b),
                SUM(CASE WHEN b.status = 'OVERDUE' THEN 1 ELSE 0 END)
            )
            FROM Booking b
            """)
    BookingDiagramDTO getDiagramData();

    Page<Booking> findAllBookingByGivenAtAndStatus(LocalDate givenAt, Status status, Pageable pageable);

    boolean existsBookingByStudent_Id(Long userId);
    
    @Query("""
            SELECT new aifu.project.common_domain.dto.booking_dto.BookingShortDTO(
                b.id,
                base.title,
                base.author,
                b.dueDate,
                b.givenAt,
                b.status
            )
            FROM Booking b
            JOIN b.book copy
            JOIN copy.book base
            WHERE b.student.id = :id
            and b.status in :statuses
            """)
    Page<BookingShortDTO> findAllBookingShortDTOByStudentId(Long id, List<Status> statuses, Pageable pageable);

    @Query("""
            SELECT new aifu.project.common_domain.dto.booking_dto.BookingShortDTO(
                b.id,
                base.title,
                base.author,
                b.dueDate,
                b.givenAt,
                b.status
            )
            FROM Booking b
            JOIN b.book copy
            JOIN copy.book base
            WHERE b.student.cardNumber = :query
            AND b.status in :statuses
            """)
    Page<BookingShortDTO> findAllBookingShortDTOByStudentCardNumber(String query, List<Status> statuses, Pageable pageable);

    @Query("""
            SELECT new aifu.project.common_domain.dto.booking_dto.BookingShortDTO(
                b.id,
                base.title,
                base.author,
                b.dueDate,
                b.givenAt,
                b.status
            )
            FROM Booking b
            JOIN b.book copy
            JOIN copy.book base
            WHERE LOWER(b.student.name) LIKE LOWER(CONCAT('%', :query, '%'))
            AND b.status in :statuses
            """)
    Page<BookingShortDTO> findAllBookingShortDTOByStudentName(String query, List<Status> statuses, Pageable pageable);

    @Query("""
            SELECT new aifu.project.common_domain.dto.booking_dto.BookingShortDTO(
                b.id,
                base.title,
                base.author,
                b.dueDate,
                b.givenAt,
                b.status
            )
            FROM Booking b
            JOIN b.book copy
            JOIN copy.book base
            WHERE copy.epc = :query
            AND b.status in :statuses
            """)
    Page<BookingShortDTO> findAllBookingShortDTOByBookEpc(String query, List<Status> statuses, Pageable pageable);

    @Query("""
            SELECT new aifu.project.common_domain.dto.booking_dto.BookingShortDTO(
                b.id,
                base.title,
                base.author,
                b.dueDate,
                b.givenAt,
                b.status
            )
            FROM Booking b
            JOIN b.book copy
            JOIN copy.book base
            WHERE copy.inventoryNumber = :query
            AND b.status in :statuses
            """)
    Page<BookingShortDTO> findAllBookingShortDTOByBookInventoryNumber(String query, List<Status> statuses, Pageable pageable);

}
