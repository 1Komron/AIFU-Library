package aifu.project.commondomain.repository;

import aifu.project.commondomain.entity.Booking;
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
                       WHERE b.user.chatId = :chatId
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
}
