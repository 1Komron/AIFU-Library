package aifu.project.librarybot.handler;

import aifu.project.common_domain.exceptions.BookingNotFoundException;
import aifu.project.common_domain.exceptions.UserDeletionException;
import aifu.project.common_domain.exceptions.UserNotFoundException;
import aifu.project.common_domain.dto.ResponseMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<ResponseMessage> handleUserNotFoundException(UserNotFoundException e) {
        log.error("User not found. Message: {}", e.getMessage());

        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ResponseMessage(false, e.getMessage(), null));
    }

    @ExceptionHandler(UserDeletionException.class)
    public ResponseEntity<ResponseMessage> handleUserDeletionException(UserDeletionException e) {
        log.error("User deletion exception. Message: {}", e.getMessage());

        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ResponseMessage(false, e.getMessage(), null));
    }

    @ExceptionHandler(BookingNotFoundException.class)
    public ResponseEntity<ResponseMessage> handleBookingNotFoundException(BookingNotFoundException e) {
        log.error("Booking not found. Message: {}", e.getMessage());

        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ResponseMessage(false, e.getMessage(), null));
    }
}
