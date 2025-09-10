package aifu.project.libraryweb.repository;

import aifu.project.common_domain.dto.booking_dto.BookingResponse;
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
import java.util.Set;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {

    @Query("""
            SELECT new aifu.project.common_domain.dto.booking_dto.BookingShortDTO(
                b.id,
                    s.name,
                    s.surname,
                    base.title,
                    base.author,
                    b.dueDate,
                    b.givenAt,
                    b.status
            )
            FROM Booking b
            JOIN b.book copy
            JOIN copy.book base
            JOIN b.student s
            where b.student = :student
            """)
    List<BookingShortDTO> findAllStudentBookingShortDTO(Student student);

    @Query("""
            SELECT new aifu.project.common_domain.dto.booking_dto.BookingShortDTO(
                b.id,
                    s.name,
                    s.surname,
                    base.title,
                    base.author,
                    b.dueDate,
                    b.givenAt,
                    b.status
            )
            FROM Booking b
            JOIN b.book copy
            JOIN copy.book base
            JOIN b.student s
            where b.status in :statuses
            """)
    Page<BookingShortDTO> findAllBookingShortDTO(Pageable pageable, List<Status> statuses);

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
                s.surname,
                s.degree,
                s.faculty,
                s.cardNumber
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
                SUM(CASE WHEN b.status != 'OVERDUE' THEN 1 ELSE 0 END),
                SUM(CASE WHEN b.status = 'OVERDUE' THEN 1 ELSE 0 END)
            )
            FROM Booking b
            """)
    BookingDiagramDTO getDiagramData();

    @Query("""
            select new aifu.project.common_domain.dto.booking_dto.BookingResponse(
            concat(s.name, ' ', s.surname),
                concat(l.name, ' ', l.surname),
            book.author,
            book.title,
            b.dueDate
            )
            from Booking b
            join b.book.book book
            join b.student s
            join b.issuedBy l
            where b.givenAt = :givenAt
            and b.status = :status
            """)
    List<BookingResponse> findAllBookingByGivenAtAndStatus(LocalDate givenAt, Status status);

    boolean existsBookingByStudent_Id(Long userId);

    @Query("""
            SELECT new aifu.project.common_domain.dto.booking_dto.BookingShortDTO(
               b.id,
                    s.name,
                    s.surname,
                    base.title,
                    base.author,
                    b.dueDate,
                    b.givenAt,
                    b.status
            )
            FROM Booking b
            JOIN b.book copy
            JOIN copy.book base
            JOIN b.student s
            WHERE b.student.cardNumber = :query
            AND b.status in :statuses
            """)
    Page<BookingShortDTO> findAllBookingShortDTOByStudentCardNumber(String query, List<Status> statuses, Pageable pageable);

    @Query("""
            SELECT new aifu.project.common_domain.dto.booking_dto.BookingShortDTO(
                b.id,
                    s.name,
                    s.surname,
                    base.title,
                    base.author,
                    b.dueDate,
                    b.givenAt,
                    b.status
            )
            FROM Booking b
            JOIN b.book copy
            JOIN copy.book base
            JOIN b.student s
            WHERE copy.epc = :query
            AND b.status in :statuses
            """)
    Page<BookingShortDTO> findAllBookingShortDTOByBookEpc(String query, List<Status> statuses, Pageable pageable);

    @Query("""
                SELECT new aifu.project.common_domain.dto.booking_dto.BookingShortDTO(
                    b.id,
                    s.name,
                    s.surname,
                    base.title,
                    base.author,
                    b.dueDate,
                    b.givenAt,
                    b.status
                )
                FROM Booking b
                JOIN b.book copy
                JOIN copy.book base
                JOIN b.student s
                WHERE
                  (
                    (:first IS NOT NULL AND (LOWER(s.name) LIKE (:first)))
                    OR
                    (:second IS NOT NULL AND (LOWER(s.surname) LIKE (:second)))
                  )
                  AND b.status in :statuses
            """)
    Page<BookingShortDTO> findAllBookingShortDTOByStudentFullName(String first, String second, List<Status> statuses, Pageable pageable);

    @Query("""
            SELECT new aifu.project.common_domain.dto.booking_dto.BookingShortDTO(
                b.id,
                    s.name,
                    s.surname,
                    base.title,
                    base.author,
                    b.dueDate,
                    b.givenAt,
                    b.status
            )
            FROM Booking b
            JOIN b.book copy
            JOIN copy.book base
            JOIN b.student s
            WHERE copy.inventoryNumber = :query
            AND b.status in :statuses
            """)
    Page<BookingShortDTO> findAllBookingShortDTOByBookInventoryNumber(String query, List<Status> statuses, Pageable pageable);

    @Query("SELECT b.student.id FROM Booking b WHERE b.student.id IN :studentIds")
    Set<Long> findStudentIdsWithActiveBookings(@Param("studentIds") Set<Long> studentIds);

    @Query("select b from  Booking b where b.student.id = :id")
    List<Booking> finAllByStudent(Long id);

    @Query("""
            SELECT new aifu.project.common_domain.dto.booking_dto.BookingShortDTO(
                b.id,
                    s.name,
                    s.surname,
                    base.title,
                    base.author,
                    b.dueDate,
                    b.givenAt,
                    b.status
            )
            FROM Booking b
            JOIN b.book copy
            JOIN copy.book base
            JOIN b.student s
            WHERE b.status = :status
            """)
    List<BookingShortDTO> findAllBookingByStatus(Status status);

    long countBookingByStatus(Status status);
}
