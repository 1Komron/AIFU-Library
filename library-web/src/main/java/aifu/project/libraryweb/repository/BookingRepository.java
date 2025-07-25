package aifu.project.libraryweb.repository;

import aifu.project.common_domain.dto.BookingDiagramDTO;
import aifu.project.common_domain.dto.BookingShortDTO;
import aifu.project.common_domain.dto.BookingSummaryDTO;
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
import java.util.Optional;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {
    @Query("""
            SELECT new aifu.project.common_domain.dto.BookingShortDTO(
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
            SELECT new aifu.project.common_domain.dto.BookingSummaryDTO(
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

    Page<BookingShortDTO> findShortByStatus(Status statusEnum, Pageable pageable);

    Optional<Booking> findByStudentAndBook(Student student, BookCopy bookCopy);

    @Query("""
            SELECT new aifu.project.common_domain.dto.BookingDiagramDTO(
                COUNT(b),
                SUM(CASE WHEN b.status = 'OVERDUE' THEN 1 ELSE 0 END)
            )
            FROM Booking b
            """)
    BookingDiagramDTO getDiagramData();

    Page<Booking> findAllBookingByGivenAtAndStatus(LocalDate givenAt, Status status, Pageable pageable);

    boolean existsBookingByStudent_Id(Long userId);
}
