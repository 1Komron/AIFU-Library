package aifu.project.librarybot.repository;

import aifu.project.common_domain.dto.BookingShortDTO;

import aifu.project.common_domain.entity.Booking;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {

    @Query(
            value = """
                    SELECT DISTINCT b
                    FROM Booking b
                    JOIN FETCH b.book bc
                    JOIN FETCH bc.book bb
                    WHERE b.student.chatId = :chatId
                    ORDER BY b.givenAt DESC
                    """,
            countQuery = """
                    SELECT COUNT(b)
                    FROM Booking b
                    WHERE b.student.chatId = :chatId
                    """
    )
    Page<Booking> findAllWithBooksByUserChatId(@Param("chatId") Long chatId, Pageable pageable);

    Booking findByBookIdAndStudentChatId(Integer bookId, Long chatId);

    @Query("""
            SELECT b
            FROM Booking b
            JOIN FETCH b.student u
            WHERE b.dueDate <= :now
            """)
    List<Booking> findExpiredBookings(@Param("now") LocalDate now);

    @Query("""
            SELECT b
            FROM Booking b
            JOIN FETCH b.student u
            WHERE b.dueDate = :tomorrow
            """)
    List<Booking> findByDueDate(@Param("tomorrow") LocalDate tomorrow);

    @Query("SELECT b FROM Booking b WHERE b.student.chatId = :chatId AND b.dueDate <= :date")
    List<Booking> findAllExpiredBookings(@Param("chatId") Long chatId, @Param("date") LocalDate date);

    @Query(
            value = """
                    SELECT b
                    FROM Booking b
                    JOIN FETCH b.book bc
                    JOIN FETCH bc.book bb
                    WHERE b.student.chatId = :chatId
                    AND b.dueDate <= :date
                    """,
            countQuery = """
                    SELECT COUNT(b)
                    FROM Booking b
                    WHERE b.student.chatId = :chatId
                    AND b.dueDate = :tomorrow
                    """
    )
    Page<Booking> findAllExpiredOverdue(@Param("chatId") Long chatId, @Param("date") LocalDate date, Pageable pageable);

    Booking findBookingByStudent_ChatIdAndBook_InventoryNumber(Long userChatId, String bookInventoryNumber);

    @Query(
            value = """
                    SELECT b
                    FROM Booking b
                    JOIN FETCH b.book bc
                    JOIN FETCH bc.book bb
                    WHERE b.student.chatId = :chatId
                    AND b.dueDate <= :tomorrow
                    """,
            countQuery = """
                    SELECT COUNT(b)
                    FROM Booking b
                    WHERE b.student.chatId = :chatId
                    AND b.dueDate = :tomorrow
                    """
    )
    Page<Booking> findAllExpiringOverdue(@Param("chatId") Long chatId, @Param("tomorrow") LocalDate tomorrow, Pageable pageable);

    @Query("SELECT b FROM Booking b WHERE b.student.chatId = :chatId AND b.dueDate = :tomorrow")
    List<Booking> findAllExpiringBookings(@Param("chatId") Long chatId, @Param("tomorrow") LocalDate tomorrow);

    boolean existsBookingByStudent_Id(Long userId);

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
}
