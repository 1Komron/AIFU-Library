package aifu.project.libraryweb.service.statistics_service;

import aifu.project.common_domain.dto.ResponseMessage;
import org.springframework.http.ResponseEntity;

public interface StatisticsService {
    ResponseEntity<ResponseMessage> countAllBookings();

    ResponseEntity<ResponseMessage> getBookingDiagram();

    ResponseEntity<ResponseMessage> getBookingToday(int pageNumber, int pageSize);

    ResponseEntity<ResponseMessage> getBookingTodayOverdue(int pageNumber, int pageSize);

    ResponseEntity<ResponseMessage> countUsers();

    ResponseEntity<ResponseMessage> countBooks();

    ResponseEntity<ResponseMessage> countBookCopies();

    // Kunlik statistikalar
    ResponseEntity<ResponseMessage> getBookingPerDay(int month, int year);

    // Oylik statistikalar
    ResponseEntity<ResponseMessage> getBookingPerMonth(int year);

    // Eng ko'p o'qilgan kitoblar
    ResponseEntity<ResponseMessage> getTopPopularBooks();

    // Eng ko'p kitob o'qigan talabalar
    ResponseEntity<ResponseMessage> getTopStudents();

    //Kitoblarning o'rtacha foydalanish kunlari
    ResponseEntity<ResponseMessage> getAverageUsageDays();
}
