package aifu.project.libraryweb.repository;

import aifu.project.common_domain.dto.BookingShortDTO;
import aifu.project.common_domain.dto.BookingSummaryDTO;
import aifu.project.common_domain.entity.BookCopy;
import aifu.project.common_domain.entity.Booking;
import aifu.project.common_domain.entity.User;
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
//    @Query(
//            value = """
//                      SELECT DISTINCT b
//                        FROM Booking b
//                        JOIN FETCH b.book bc
//                        JOIN FETCH bc.book bb
//                       WHERE b.user.chatId = :chatId
//                    """,
//            countQuery = """
//                      SELECT COUNT(b)
//                        FROM Booking b
//                       WHERE b.user.chatId = :chatId
//                    """
//    )
//    Page<Booking> findAllWithBooksByUserChatId(@Param("chatId") Long chatId, Pageable pageable);
//
//
//    Booking findByBookIdAndUserChatId(Integer bookId, Long chatId);
//
//    List<Booking> findAllWithBooksByUser_ChatId(Long chatId);
//
//    @Query("""
//                SELECT b
//                  FROM Booking b
//                  JOIN FETCH b.user u
//                 WHERE b.dueDate < :now
//            """)
//    List<Booking> findByDueDateBefore(LocalDate now);
//
//    @Query("""
//                SELECT b
//                  FROM Booking b
//                  JOIN FETCH b.user u
//                 WHERE b.dueDate = :tomorrow
//            """)
//    List<Booking> findByDueDate(LocalDate tomorrow);

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
                u.id,
                u.name,
                u.surname
            )
            FROM Booking b
            JOIN b.book copy
            JOIN copy.book base
            JOIN b.user u
            WHERE b.id = :id
            """)
    Optional<BookingSummaryDTO> findSummary(@Param("id") Long id);

    Page<BookingShortDTO> findShortByStatus(Status statusEnum, Pageable pageable);

    Optional<Booking> findByUserAndBook(User user, BookCopy bookCopy);
}
