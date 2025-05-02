package aifu.project.librarybot.controller;

import aifu.project.commondomain.payload.*;
import aifu.project.librarybot.service.ResponseService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController()
@RequestMapping("/response")
@RequiredArgsConstructor
public class ResponseController {
    private final ResponseService responseService;

    @PostMapping("/registration")
    public ResponseEntity<ResponseMessage> registration(@RequestBody RegistrationResponse registrationResponse) {
       return responseService.sendRegistrationMessage(registrationResponse);
    }

    @PostMapping("/book/borrow")
    public ResponseEntity<ResponseMessage> borrowBookResponse(@RequestBody BorrowBookResponse borrowBookResponse) {
        return responseService.borrowBookResponse(borrowBookResponse);
    }

    @PostMapping("/book/extend")
    public ResponseEntity<ResponseMessage> extendBookResponse(@RequestBody ExtendBookResponse extendBookResponse) {
        return responseService.extendBookResponse(extendBookResponse);
    }

    @PostMapping("/book/return")
    public ResponseEntity<ResponseMessage> returnBookResponse(@RequestBody ReturnBookResponse response) {
        return responseService.returnBookResponse(response);
    }
}
