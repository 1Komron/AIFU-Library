package aifu.project.libraryweb.handler; // Using your existing package

import aifu.project.common_domain.exceptions.*; // Assuming this is where your other exceptions are
import aifu.project.common_domain.dto.ResponseMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice; // Use RestControllerAdvice

import java.util.stream.Collectors;

@Slf4j
@RestControllerAdvice // Use RestControllerAdvice for global REST controller handling
public class GlobalExceptionHandler {

    // --- NEW HANDLER FOR OUR ADMIN MODULE ---
    @ExceptionHandler(EmailAlreadyExistsException.class)
    public ResponseEntity<ResponseMessage> handleEmailAlreadyExistsException(EmailAlreadyExistsException e) {
        log.error("Attempted to create a user with an existing email. Message: {}", e.getMessage());
        // 409 Conflict is the most appropriate status for a resource that already exists.
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(new ResponseMessage(false, e.getMessage(), null));
    }

    // --- UPDATED HANDLER TO USE ResponseMessage ---
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ResponseMessage> handleValidationExceptions(MethodArgumentNotValidException ex) {
        // Collect all validation error messages into a single string.
        String errors = ex.getBindingResult().getFieldErrors().stream()
                .map(error -> String.format("'%s': %s", error.getField(), error.getDefaultMessage()))
                .collect(Collectors.joining(", "));

        log.warn("Validation failed: {}", errors);

        // Return the errors in the consistent ResponseMessage format.
        return ResponseEntity.badRequest()
                .body(new ResponseMessage(false, "Validation failed: " + errors, null));
    }

    // --- Your Existing Handlers (Unchanged) ---
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

    // --- UPDATED CATCH-ALL HANDLER TO USE ResponseMessage ---
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ResponseMessage> handleRuntimeException(RuntimeException ex) {
        // This is a catch-all for unexpected errors. We should not expose the raw message to the user.
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

    @ExceptionHandler(NumberFormatException.class)
    public ResponseEntity<ResponseMessage> handleNumberFormatException(NumberFormatException e) {
        log.error("Number format exception. Message: {}", e.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ResponseMessage(false, "Noto'g'ri format kitildi. Kutilgan format son: " + e.getMessage(), null));
    }
}