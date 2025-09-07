package aifu.project.libraryweb.handler;

import aifu.project.common_domain.exceptions.*; // Assuming this is where your other exceptions are
import aifu.project.common_domain.dto.ResponseMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.stream.Collectors;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(BookImportNonValidHeaderException.class)
    public ResponseEntity<ResponseMessage> handleBookImportNonValidHeaderException(BookImportNonValidHeaderException e) {
        System.out.println("check");
        System.out.println(e.getErrors());
        log.error("Exel orqali kitob qo'shida xatolik yuz berdi. Message: {}, Errors: {}", e.getMessage(), e.getErrors());
        return ResponseEntity.badRequest().body(new ResponseMessage(false, e.getMessage(), e.getErrors()));
    }

    @ExceptionHandler(CardNumberAlreadyExistsException.class)
    public ResponseEntity<ResponseMessage> handleCardNumberAlreadyExistsException(CardNumberAlreadyExistsException e) {
        log.error("CardNumber tizimda mavjud. Message: {}", e.getMessage());
        return ResponseEntity.status(HttpStatus.CONFLICT).build();
    }

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<ResponseMessage> handleUserNotFoundException(UserNotFoundException e) {
        log.error("Foydalanuvchi topilmadi. Message: {}", e.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new ResponseMessage(false, e.getMessage(), null));
    }

    @ExceptionHandler(UserAlreadyExistsException.class)
    public ResponseEntity<ResponseMessage> handleUserAlreadyExistsExceptions(UserAlreadyExistsException e) {
        log.error("User tizimda mavjud. Message: {}", e.getMessage());
        return ResponseEntity.status(HttpStatus.CONFLICT).build();
    }

    @ExceptionHandler(EmailAlreadyExistsException.class)
    public ResponseEntity<ResponseMessage> handleEmailAlreadyExistsException(EmailAlreadyExistsException e) {
        log.error("Attempted to create a user with an existing email. Message: {}", e.getMessage());
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(new ResponseMessage(false, e.getMessage(), null));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ResponseMessage> handleValidationExceptions(MethodArgumentNotValidException ex) {
        String errors = ex.getBindingResult().getFieldErrors().stream()
                .map(error -> String.format("'%s': %s", error.getField(), error.getDefaultMessage()))
                .collect(Collectors.joining(", "));

        log.warn("Validation failed: {}", errors);

        return ResponseEntity.badRequest()
                .body(new ResponseMessage(false, "Validation failed: " + errors, null));
    }

    @ExceptionHandler(BaseBookCategoryNotFoundException.class)
    public ResponseEntity<ResponseMessage> handleBaseBookCategoryNotFoundException(BaseBookCategoryNotFoundException e) {
        log.error("Base book category not found. Message: {}", e.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new ResponseMessage(false, e.getMessage(), null));
    }

    @ExceptionHandler(BaseBookNotFoundException.class)
    public ResponseEntity<ResponseMessage> handleBaseBookNotFoundException(BaseBookNotFoundException e) {
        log.error("Base book not found. Message: {}", e.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new ResponseMessage(false, e.getMessage(), null));
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ResponseMessage> handleIllegalArgumentException(IllegalArgumentException e) {
        log.error("Illegal argument exception. Message: {}", e.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST) // 400 is more suitable for illegal arguments
                .body(new ResponseMessage(false, e.getMessage(), null));
    }

    @ExceptionHandler(BookCopyIsTakenException.class)
    public ResponseEntity<ResponseMessage> handleBookCopyIsTakenException(BookCopyIsTakenException e) {
        log.error("Book copy has been taken. Message: {}", e.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ResponseMessage(false, e.getMessage(), null));
    }

    @ExceptionHandler(CategoryDeletionException.class)
    public ResponseEntity<ResponseMessage> handleCategoryDeletionException(CategoryDeletionException e) {
        log.error("Category deletion error. Message: {}", e.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ResponseMessage(false, e.getMessage(), null));
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ResponseMessage> handleRuntimeException(RuntimeException ex) {
        log.error("An unexpected runtime exception occurred: {}", ex.getMessage(), ex);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ResponseMessage(false, "An unexpected internal server error occurred.", null));
    }

    @ExceptionHandler(NotificationNotFoundException.class)
    public ResponseEntity<ResponseMessage> handleNotificationNotFoundException(NotificationNotFoundException e) {
        log.error("Notification not found. Message: {}", e.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new ResponseMessage(false, e.getMessage(), null));
    }

    @ExceptionHandler(LoginBadCredentialsException.class)
    public ResponseEntity<ResponseMessage> handleLoginBadCredentialsException(LoginBadCredentialsException e) {
        log.error("Login bad credentials exception. Message: {}", e.getMessage());
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(new ResponseMessage(false, e.getMessage(), null));
    }

    @ExceptionHandler(BookCopyNotFoundException.class)
    public ResponseEntity<ResponseMessage> handleBookCopyNotFoundException(BookCopyNotFoundException e) {
        log.error("Book copy not found. Message: {}", e.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new ResponseMessage(false, e.getMessage(), null));
    }

    @ExceptionHandler(CategoryNotFoundException.class)
    public ResponseEntity<ResponseMessage> handleCategoryNotFoundException(CategoryNotFoundException e) {
        log.error("Category not found. Message: {}", e.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new ResponseMessage(false, e.getMessage(), null));
    }

    @ExceptionHandler(HistoryNotFoundException.class)
    public ResponseEntity<ResponseMessage> handleHistoryNotFoundException(HistoryNotFoundException e) {
        log.error("History not found. Message: {}", e.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new ResponseMessage(false, e.getMessage(), null));
    }

    @ExceptionHandler(StudentImportException.class)
    public ResponseEntity<ResponseMessage> handleStudentImportException(StudentImportException e) {
        log.warn("Student import failed. Message: {}", e.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ResponseMessage(false, e.getMessage(), null));
    }

    @ExceptionHandler(BookingNotFoundException.class)
    public ResponseEntity<ResponseMessage> handleBookingNotFoundException(BookingNotFoundException e) {
        log.error("Booking not found. Message: {}", e.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new ResponseMessage(false, e.getMessage(), null));
    }

    @ExceptionHandler(PdfBookNotFoundException.class)
    public ResponseEntity<ResponseMessage> handlePdfBookNotFoundException(PdfBookNotFoundException e) {
        log.error("PDF book not found. Message: {}", e.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new ResponseMessage(false, e.getMessage(), null));
    }

    @ExceptionHandler(PdfFileDownloadException.class)
    public ResponseEntity<ResponseMessage> handlePdfFileDownloadException(PdfFileDownloadException e) {
        log.error("PDF file download failed. Message: {}", e.getMessage(), e);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ResponseMessage(false, "Failed to download PDF file.", null));
    }

    @ExceptionHandler(FileValidationException.class)
    public ResponseEntity<ResponseMessage> handleFileValidationException(FileValidationException e) {
        log.warn("File validation failed. Message: {}", e.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ResponseMessage(false, e.getMessage(), null));
    }

    @ExceptionHandler(FileUploadException.class)
    public ResponseEntity<ResponseMessage> handleFileUploadException(FileUploadException e) {
        log.error("File upload to external storage failed. Message: {}", e.getMessage(), e);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ResponseMessage(false, "Could not upload the file. Please try again later.", null));
    }

    @ExceptionHandler(ReportNotFoundException.class)
    public ResponseEntity<ResponseMessage> handleReportNotFoundException(ReportNotFoundException e) {
        log.warn("Hisobot topilmadi. Sababi: {}", e.getMessage());
        ResponseMessage responseBody = new ResponseMessage(false, e.getMessage(), null);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        return new ResponseEntity<>(responseBody, headers, HttpStatus.NOT_FOUND); // 404
    }

    @ExceptionHandler(ReportGenerationException.class)
    public ResponseEntity<ResponseMessage> handleReportGenerationException(ReportGenerationException e) {
        log.error("Hisobot generatsiyasida kutilmagan xatolik: {}", e.getMessage(), e.getCause());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ResponseMessage(false, "Hisobotni yaratishda serverda xatolik yuz berdi. Iltimos, keyinroq qayta urinib ko'ring.", null));
    }

}