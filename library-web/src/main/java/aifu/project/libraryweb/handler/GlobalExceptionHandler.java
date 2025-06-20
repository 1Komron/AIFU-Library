package aifu.project.libraryweb.handler;

import aifu.project.common_domain.exceptions.BaseBookCategoryNotFoundException;
import aifu.project.common_domain.exceptions.BaseBookNotFoundException;
import aifu.project.common_domain.exceptions.BookCopyIsTakenException;
import aifu.project.common_domain.exceptions.CategoryDeletionException;
import aifu.project.common_domain.payload.ResponseMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler {

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

        return ResponseEntity.status(HttpStatus.NOT_FOUND)
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
        log.error("Category deletion has been deleted. Message: {}", e.getMessage());

        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ResponseMessage(false, e.getMessage(), null));
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<String> handleRuntimeException(RuntimeException ex) {
        return ResponseEntity.badRequest().body(ex.getMessage());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(error ->
                errors.put(error.getField(), error.getDefaultMessage()));
        return ResponseEntity.badRequest().body(errors);
    }
}
