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
}
