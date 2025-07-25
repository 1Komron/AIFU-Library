package aifu.project.libraryweb.service;

import aifu.project.common_domain.dto.*;
import aifu.project.common_domain.dto.live_dto.BaseBookCategoryDTO;
import aifu.project.common_domain.dto.statistic_dto.*;
import aifu.project.common_domain.entity.enums.Status;
import aifu.project.common_domain.payload.ResponseMessage;
import aifu.project.libraryweb.service.base_book_service.BaseBookServiceImpl;
import aifu.project.libraryweb.service.base_book_service.BookCopyService;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;

@Service
@RequiredArgsConstructor
public class StatisticsService {
    private final BaseBookServiceImpl bookService;
    private final StudentService studentService;
    private final BookCopyService bookCopyService;
    private final BookingService bookingService;

    @PersistenceContext
    private EntityManager em;

    public ResponseEntity<ResponseMessage> countAllBookings() {
        long count = bookingService.countAllBookings();
        return ResponseEntity.ok(new ResponseMessage(true, "Booking count", count));
    }

    public ResponseEntity<ResponseMessage> getBookingDiagram() {
        BookingDiagramDTO diagram = bookingService.getBookingDiagram();
        return ResponseEntity.ok(new ResponseMessage(true, "Booking diagram", diagram));
    }

    public ResponseEntity<ResponseMessage> getBookingToday(int pageNumber, int pageSize) {
        List<BookingResponse> list = bookingService.getListBookingsToday(--pageNumber, pageSize, Status.APPROVED);
        return ResponseEntity.ok(new ResponseMessage(true, "Today's booking list", list));
    }

    public ResponseEntity<ResponseMessage> getBookingTodayOverdue(int pageNumber, int pageSize) {
        List<BookingResponse> list = bookingService.getListBookingsToday(--pageNumber, pageSize, Status.OVERDUE);
        return ResponseEntity.ok(new ResponseMessage(true, "Today's booking list", list));
    }

    public ResponseEntity<ResponseMessage> countUsers() {
        long count = studentService.countStudents();
        return ResponseEntity.ok(new ResponseMessage(true, "Users count", count));
    }

    public ResponseEntity<ResponseMessage> countBooks() {
        long count = bookService.countBooks();
        return ResponseEntity.ok(new ResponseMessage(true, "Books count", count));
    }

    public ResponseEntity<ResponseMessage> countBookCopies() {
        long count = bookCopyService.count();
        return ResponseEntity.ok(new ResponseMessage(true, "Book copies count", count));
    }

    // Kunlik statistikalar
    public ResponseEntity<ResponseMessage> getBookingPerDay(int month, int year) {
        if (month < 1 || month > 12) {
            throw new IllegalArgumentException("Invalid month");
        }

        YearMonth ym = YearMonth.of(year, month);
        LocalDate start = ym.atDay(1);
        LocalDate endInclusive = ym.atEndOfMonth();

        @SuppressWarnings("unchecked")
        List<Object[]> rows = em.createNativeQuery("""
                        WITH bounds AS (
                            SELECT CAST(:startDate AS date) AS start_date,
                                   CAST(:endInclusive AS date) AS end_date
                        ),
                        days AS (
                            SELECT (b.start_date + gs.d)::date AS day
                            FROM bounds b
                            JOIN generate_series(0, (b.end_date - b.start_date)) AS gs(d) ON true
                            WHERE EXTRACT(DOW FROM (b.start_date + gs.d)) <> 0   -- исключаем воскресенье (0)
                        ),
                        taken AS (
                            SELECT b.given_at::date AS day
                            FROM booking b, bounds
                            WHERE b.given_at BETWEEN start_date AND end_date
                        
                            UNION ALL
                        
                            SELECT h.given_at::date AS day
                            FROM history h, bounds
                            WHERE h.given_at BETWEEN start_date AND end_date
                        ),
                        returned AS (
                            SELECT h.returned_at::date AS day
                            FROM history h, bounds
                            WHERE h.returned_at BETWEEN start_date AND end_date
                        ),
                        returned_late AS (
                            SELECT h.returned_at::date AS day
                            FROM history h, bounds
                            WHERE h.returned_at BETWEEN start_date AND end_date
                              AND h.returned_at > h.due_date
                        )
                        SELECT
                            d.day AS date,
                            COUNT(t.day)  AS taken,
                            COUNT(r.day)  AS returned,
                            COUNT(rl.day) AS returned_late
                        FROM days d
                        LEFT JOIN taken         t  ON t.day  = d.day
                        LEFT JOIN returned      r  ON r.day  = d.day
                        LEFT JOIN returned_late rl ON rl.day = d.day
                        GROUP BY d.day
                        ORDER BY d.day
                        """)
                .setParameter("startDate", start)
                .setParameter("endInclusive", endInclusive)
                .getResultList();

        List<BookingsPerDayDTO> list = rows.stream()
                .map(r -> new BookingsPerDayDTO(
                        ((java.sql.Date) r[0]).toLocalDate(),
                        ((Number) r[1]).intValue(),
                        ((Number) r[2]).intValue(),
                        ((Number) r[3]).intValue()
                ))
                .toList();

        return ResponseEntity.ok(new ResponseMessage(true, "Booking list", list));
    }


    // Oylik statistikalar
    public ResponseEntity<ResponseMessage> getBookingPerMonth(int year) {
        @SuppressWarnings("unchecked")
        List<Object[]> rows = em.createNativeQuery("""
                            WITH months AS (
                                SELECT generate_series(1, 12) AS month
                            ),
                            taken AS (
                                SELECT mth AS month, COUNT(*) AS cnt
                                FROM (
                                    SELECT EXTRACT(MONTH FROM b.given_at)::int AS mth
                                    FROM booking b
                                    WHERE EXTRACT(YEAR FROM b.given_at) = :year
                        
                                    UNION ALL
                        
                                    SELECT EXTRACT(MONTH FROM h.given_at)::int AS mth
                                    FROM history h
                                    WHERE EXTRACT(YEAR FROM h.given_at) = :year
                                ) t
                                GROUP BY mth
                            ),
                            returned AS (
                                SELECT EXTRACT(MONTH FROM h.returned_at)::int AS month,
                                       COUNT(*) AS cnt
                                FROM history h
                                WHERE EXTRACT(YEAR FROM h.returned_at) = :year
                                GROUP BY EXTRACT(MONTH FROM h.returned_at)
                            ),
                            returned_late AS (
                                SELECT EXTRACT(MONTH FROM h.returned_at)::int AS month,
                                       COUNT(*) AS cnt
                                FROM history h
                                WHERE EXTRACT(YEAR FROM h.returned_at) = :year
                                  AND h.returned_at > h.due_date
                                GROUP BY EXTRACT(MONTH FROM h.returned_at)
                            )
                            SELECT
                                m.month,
                                COALESCE(t.cnt, 0)  AS taken,
                                COALESCE(r.cnt, 0)  AS returned,
                                COALESCE(rl.cnt, 0) AS returned_late
                            FROM months m
                            LEFT JOIN taken         t  USING (month)
                            LEFT JOIN returned      r  USING (month)
                            LEFT JOIN returned_late rl USING (month)
                            ORDER BY m.month
                        """)
                .setParameter("year", year)
                .getResultList();

        List<BookingsPerMonthDTO> list = rows.stream()
                .map(r -> new BookingsPerMonthDTO(
                        ((Number) r[0]).intValue(),
                        ((Number) r[1]).intValue(),
                        ((Number) r[2]).intValue(),
                        ((Number) r[3]).intValue()
                ))
                .toList();

        return ResponseEntity.ok(new ResponseMessage(true, "Statistics by month for year " + year, list));
    }


    // Eng ko'p o'qilgan kitoblar
    public ResponseEntity<ResponseMessage> getTopPopularBooks(int limit) {

        @SuppressWarnings("unchecked")
        List<Object[]> rows = em.createNativeQuery("""
                            SELECT
                                b.title,
                                b.author,
                                c.id,
                                c.name,
                                b.isbn,
                                COUNT(*) AS usage_count
                            FROM (
                                SELECT book_id
                                FROM booking
                                UNION ALL
                                SELECT book_id
                                FROM history
                            ) AS all_bookings
                            JOIN book_copy bc ON bc.id = all_bookings.book_id
                            JOIN base_book b ON b.id = bc.base_book_id
                            JOIN category c ON c.id = b.category_id
                            where b.is_deleted = false
                            GROUP BY b.id, b.title, b.author, c.id, c.name, b.isbn
                            ORDER BY usage_count DESC
                            LIMIT :limit
                        """)
                .setParameter("limit", limit)
                .getResultList();


        List<TopBookDTO> topBooks = rows.stream()
                .map(r -> new TopBookDTO(
                        (String) r[0], // title
                        (String) r[1], // author
                        new BaseBookCategoryDTO(
                                ((Number) r[2]).intValue(),
                                (String) r[3]),
                        (String) r[4], // isbn
                        ((Number) r[5]).intValue() // usageCount
                ))
                .toList();

        return ResponseEntity.ok(new ResponseMessage(true, "Top popular books", topBooks));
    }

    // Eng ko'p kitob o'qigan talabalar
    public ResponseEntity<ResponseMessage> getTopStudents(int limit) {
        @SuppressWarnings("unchecked")
        List<Object[]> rows = em.createNativeQuery("""
                            SELECT
                                u.name,
                                u.surname,
                                s.degree,
                                s.faculty,
                                COUNT(*) AS usage_count
                            FROM (
                                SELECT student_id FROM booking
                                UNION ALL
                                SELECT user_id FROM history
                            ) all_usages
                            JOIN "users" u ON u.id = all_usages.student_id
                            JOIN student s ON s.id = u.id
                            WHERE u.is_deleted = false
                            GROUP BY u.id, u.name, u.surname, s.degree, s.faculty
                            ORDER BY usage_count DESC
                            LIMIT :limit
                        """)
                .setParameter("limit", limit)
                .getResultList();

        List<TopStudentDTO> topStudents = rows.stream()
                .map(r -> new TopStudentDTO(
                        (String) r[0], // name
                        (String) r[1], // surname
                        (String) r[2], // degree
                        (String) r[3], // faculty
                        ((Number) r[4]).intValue() // usageCount
                ))
                .toList();

        return ResponseEntity.ok(new ResponseMessage(true, "Top students list", topStudents));
    }

    //Kitoblarning o'rtacha foydalanish kunlari
    public ResponseEntity<ResponseMessage> getAverageUsageDays() {
        Object result = em.createNativeQuery("""
                    SELECT ROUND(AVG(h.returned_at - h.given_at))
                    FROM history h
                    JOIN book_copy bc ON bc.id = h.book_id
                    JOIN base_book b ON b.id = bc.base_book_id
                    WHERE b.is_deleted = false
                      AND h.returned_at IS NOT NULL
                """).getSingleResult();

        int average = result != null ? ((Number) result).intValue() : 0;
        AverageUsageDTO averageUsage = new AverageUsageDTO(recommendLendingPeriod(average));

        return ResponseEntity.ok(new ResponseMessage(
                true,
                "Average book usage duration in days",
                averageUsage
        ));
    }

    // Tavsiya etilgan kitob berish muddati
    private String recommendLendingPeriod(int average) {
        if (average <= 3) {
            return "3";
        } else if (average <= 6) {
            return "4-6";
        } else if (average <= 10) {
            return "5-7";
        } else if (average <= 15) {
            return "10-12";
        } else {
            return "14";
        }
    }
}
