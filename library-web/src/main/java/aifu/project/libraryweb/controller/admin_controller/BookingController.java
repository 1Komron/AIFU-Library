package aifu.project.libraryweb.controller.admin_controller;

import aifu.project.common_domain.dto.booking_dto.BorrowBookDTO;
import aifu.project.common_domain.dto.booking_dto.ExtendBookingDTO;
import aifu.project.common_domain.dto.booking_dto.ReturnBookDTO;
import aifu.project.common_domain.dto.ResponseMessage;
import aifu.project.libraryweb.service.booking_serivce.BookingService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin/booking")
public class BookingController {
    private final BookingService bookingService;

    @GetMapping
    @Operation(summary = "Bookinglar ro'yxatini olish")
    @ApiResponse(responseCode = "200", description = "Bookinglar ro'yxati muvaffaqiyatli qaytarildi")
    public ResponseEntity<ResponseMessage> getBookings(@RequestParam(defaultValue = "1") int pageNum,
                                                       @RequestParam(defaultValue = "10") int pageSize,
                                                       @RequestParam(defaultValue = "asc") String sortDirection) {
        return bookingService.getBookingList(pageNum, pageSize, sortDirection);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Booking ma'lmotlarini ID bo'yicha olish")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Booking ma'lumotlari muvaffaqiyatli qaytarildi"),
            @ApiResponse(responseCode = "404", description = "Booking topilmadi")
    })
    public ResponseEntity<ResponseMessage> getBookingById(@PathVariable Long id) {
        return bookingService.getBooking(id);
    }

    @GetMapping("/search")
    @Operation(summary = "Bookinglarni qidirish")
    public ResponseEntity<ResponseMessage> getBookingByStatus(@RequestParam(required = false) String field,
                                                              @RequestParam(required = false) String query,
                                                              @RequestParam(defaultValue = "all") String filter,
                                                              @RequestParam(defaultValue = "1") int pageNum,
                                                              @RequestParam(defaultValue = "10") int pageSize,
                                                              @RequestParam(defaultValue = "asc") String sortDirection) {
        return bookingService.search(field, query, filter, pageNum, pageSize, sortDirection);
    }

    @PostMapping("/borrow")
    @Operation(summary = "Kitobni bron qilish")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Kitob muvaffaqiyatli bron yaratildi"),
            @ApiResponse(responseCode = "400", description = "Bron qilishda xatolik yuz berdi. Kitob boshqa student tomonidan bron qilingan"),
            @ApiResponse(responseCode = "404", description = "Kitob yoki student topilmadi")
    })
    public ResponseEntity<ResponseMessage> borrowBook(@RequestBody BorrowBookDTO request) {
        return bookingService.borrowBook(request);
    }

    @PostMapping("/extend")
    @Operation(summary = "Bronni vaqtini uzaytirish")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Bron muvaffaqiyatli uzaytirildi"),
            @ApiResponse(responseCode = "400", description = "Bronni uzaytirishda xatolik yuz berdi. Noto'g'ri uzaytirish kunlari kiritildi"),
            @ApiResponse(responseCode = "404", description = "Bron topilmadi")
    })
    public ResponseEntity<ResponseMessage> extendBooking(@RequestBody ExtendBookingDTO request) {
        return bookingService.extendBooking(request);
    }

    @PostMapping("/return")
    @Operation(summary = "Kitobni qaytarish")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Kitob muvaffaqiyatli qaytarildi"),
            @ApiResponse(responseCode = "404", description = "Student | Kitob | Bron topilmadi")
    })
    public ResponseEntity<ResponseMessage> returnBook(@RequestBody ReturnBookDTO request) {
        return bookingService.returnBook(request);
    }
}
