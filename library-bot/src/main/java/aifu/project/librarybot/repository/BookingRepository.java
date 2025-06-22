package aifu.project.librarybot.repository;

import aifu.project.common_domain.dto.BookingDiagramDTO;
import aifu.project.common_domain.dto.BookingShortDTO;
import aifu.project.common_domain.dto.BookingSummaryDTO;
import aifu.project.common_domain.entity.Booking;
import aifu.project.common_domain.entity.enums.Status;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;


@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {
    @Query(
            value = """
                      SELECT DISTINCT b
                        FROM Booking b
                        JOIN FETCH b.book bc
                        JOIN FETCH bc.book bb
                       WHERE b.user.chatId = :chatId
                       order by b.givenAt desc
                    """,
            countQuery = """
                      SELECT COUNT(b)
                        FROM Booking b
                       WHERE b.user.chatId = :chatId
                    """
    )
    Page<Booking> findAllWithBooksByUserChatId(@Param("chatId") Long chatId, Pageable pageable);


    Booking findByBookIdAndUserChatId(Integer bookId, Long chatId);

    List<Booking> findAllWithBooksByUser_ChatId(Long chatId);

    @Query("""
                SELECT b
                  FROM Booking b
                  JOIN FETCH b.user u
                 WHERE b.dueDate < :now
            """)
    List<Booking> findByDueDateBefore(LocalDate now);

    @Query("""
                SELECT b
                  FROM Booking b
                  JOIN FETCH b.user u
                 WHERE b.dueDate = :tomorrow
            """)
    List<Booking> findByDueDate(LocalDate tomorrow);

    @Query("SELECT b FROM Booking b WHERE b.user.chatId = :chatId AND b.dueDate < :date")
    List<Booking> findAllExpiredBookings(Long chatId, LocalDate date);

    @Query(value = """
            select b
            from Booking b
            join fetch b.book bc
            join fetch bc.book bb
            where b.user.chatId = :chatId
            and b.dueDate < :date
            """,
            countQuery = """
                    SELECT COUNT(b)
                    FROM Booking b
                    WHERE b.user.chatId = :chatId
                    and b.dueDate = :tomorrow""")
    Page<Booking> findAllExpiredOverdue(Long chatId, LocalDate date, Pageable pageable);

    Booking findBookingByUser_ChatIdAndBook_InventoryNumber(Long userChatId, String bookInventoryNumber);

    @Query(value = """
            select b
            from Booking b
            join fetch b.book bc
            join fetch bc.book bb
            where b.user.chatId = :chatId
            and b.dueDate = :tomorrow
            """,
            countQuery = """
                    SELECT COUNT(b)
                    FROM Booking b
                    WHERE b.user.chatId = :chatId
                    and b.dueDate = :tomorrow""")
    Page<Booking> findAllExpiringOverdue(Long chatId, LocalDate tomorrow, Pageable pageable);

    @Query("SELECT b FROM Booking b WHERE b.user.chatId = :chatId AND b.dueDate = :tomorrow")
    List<Booking> findAllExpiringBookings(Long chatId, LocalDate tomorrow);

    @Query("""
            select new aifu.project.common_domain.dto.BookingDiagramDTO(
                   count(b),
                   sum(case when b.status = 'OVERDUE' then 1 else 0 end))
                        from Booking b""")
    BookingDiagramDTO getDiagramData();

    Page<Booking> findAllBookingByGivenAtAndStatus(LocalDate givenAt, Status status, Pageable pageable);

    long countByGivenAtBetween(LocalDate givenAtAfter, LocalDate givenAtBefore);

    boolean existsBookingByUser_Id(Long userId);

    boolean existsBookingByUser_ChatId(Long userChatId);

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
    List<BookingShortDTO> findAllBookingShortDTO();

    @Query("""
    SELECT new aifu.project.common_domain.dto.BookingSummaryDTO(
        b.id,
        base.title,
        base.author,
        copy.inventoryNumber,
        b.dueDate,
        b.givenAt,
        b.status,
        u.id,
        u.name,
        u.surname,
        u.phone
    )
    FROM Booking b
    JOIN b.book copy
    JOIN copy.book base
    JOIN b.user u
    WHERE b.id = :id
""")
    Optional<BookingSummaryDTO> findSummary(@Param("id") Long id);

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
    WHERE b.status = :status
""")
    List<BookingShortDTO> findShortByStatus(@Param("status") Status status);

}
