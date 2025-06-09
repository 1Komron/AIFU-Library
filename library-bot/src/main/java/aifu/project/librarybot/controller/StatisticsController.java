package aifu.project.librarybot.controller;

import aifu.project.commondomain.payload.ResponseMessage;
import aifu.project.librarybot.service.BookService;
import aifu.project.librarybot.service.BookingService;
import aifu.project.librarybot.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/statistics")
@RequiredArgsConstructor
public class StatisticsController {
    private final BookingService bookingService;
    private final BookService bookService;
    private final UserService userService;

    @GetMapping("/bookings/count")
    public ResponseEntity<ResponseMessage> getTotalBookings() {
        return bookingService.countAllBookings();
    }

    @GetMapping("/users/count")
    public ResponseEntity<ResponseMessage> getUserCount() {
        return userService.countUsers();
    }

    @GetMapping("/books/count")
    public ResponseEntity<ResponseMessage> getBookCount() {
        return bookService.countBooks();
    }

//    @GetMapping("/top-books")
//    public ResponseEntity<ResponseMessage> getTopBooks() {
//        return bookingService.getTopBooks(); // реализуете сортировку по популярности
//    }
}
