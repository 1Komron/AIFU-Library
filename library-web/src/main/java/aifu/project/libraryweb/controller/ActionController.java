package aifu.project.libraryweb.controller;

import aifu.project.commondomain.payload.*;
import aifu.project.libraryweb.service.boot_Service.ActionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/action")
@RequiredArgsConstructor
public class ActionController {
    private final ActionService actionService;

    @PostMapping("/registration")
    public ResponseEntity<ResponseMessage> registration(@Valid @RequestBody RegistrationRequest registrationResponse) {
        return actionService.registerResponse(registrationResponse);
    }

    @PostMapping("/book/borrow")
    public ResponseEntity<ResponseMessage> borrowBookResponse(@Valid @RequestBody BorrowBookRequest borrowBookResponse) {
        return actionService.borrowBookResponse(borrowBookResponse);
    }

    @PostMapping("/book/extend")
    public ResponseEntity<ResponseMessage> extendBookResponse(@Valid @RequestBody ExtendBookRequest extendBookResponse) {
        return actionService.extendBookResponse(extendBookResponse);
    }

    @PostMapping("/book/return")
    public ResponseEntity<ResponseMessage> returnBookResponse(@Valid @RequestBody ReturnBookRequest response) {
        return actionService.returnBookResponse(response);
    }
}
