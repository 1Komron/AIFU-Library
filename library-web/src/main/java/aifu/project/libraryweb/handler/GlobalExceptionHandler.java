package aifu.project.libraryweb.handler;

import aifu.project.common_domain.exceptions.*;
import aifu.project.common_domain.payload.ResponseMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler {


    @ExceptionHandler(EmailAlreadyExistsException.class)
    public ResponseEntity<ResponseMessage> handleEmailAlreadyExists(EmailAlreadyExistsException e) {
        ResponseMessage response = new ResponseMessage(false, e.getMessage(), null);
        return new ResponseEntity<>(response, HttpStatus.CONFLICT); // 409
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ResponseMessage> handleValidationExceptions(MethodArgumentNotValidException e) {
        String errors = e.getBindingResult().getAllErrors().stream()
                .map(error -> error.getDefaultMessage())
                .collect(Collectors.joining(", "));
        ResponseMessage response = new ResponseMessage(false, "Validation Error: " + errors, null);
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST); // 400
    }




    @ExceptionHandler(Exception.class)
    public ResponseEntity<ResponseMessage> handleAllExceptions(Exception e) {
        // In a real application, you must log the exception: log.error("Unexpected error", e);
        ResponseMessage response = new ResponseMessage(false, "An unexpected internal server error occurred.", null);
        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR); // 500
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
/*
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(error ->
                errors.put(error.getField(), error.getDefaultMessage()));
        return ResponseEntity.badRequest().body(errors);

    }*/

}
