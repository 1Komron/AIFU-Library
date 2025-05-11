package aifu.project.libraryweb.controller;

import aifu.project.commondomain.payload.*;
import aifu.project.libraryweb.service.ResponseService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController()
@RequestMapping("/response")
@RequiredArgsConstructor
public class ResponseController {
    private final ResponseService responseService;

    @PostMapping("/registration")
    public ResponseEntity<ResponseMessage> registration(@RequestBody RegistrationResponseWeb registrationResponse) {
        return responseService.sendRegistrationMessage(registrationResponse);
    }

    @PostMapping("/book/borrow")
    public ResponseEntity<ResponseMessage> borrowBookResponse(@RequestBody BorrowBookResponseWeb borrowBookResponse) {
        return responseService.borrowBookResponse(borrowBookResponse);
    }

    @PostMapping("/book/extend")
    public ResponseEntity<ResponseMessage> extendBookResponse(@RequestBody ExtendBookResponseWeb extendBookResponse) {
        return responseService.extendBookResponse(extendBookResponse);
    }

    @PostMapping("/book/return")
    public ResponseEntity<ResponseMessage> returnBookResponse(@RequestBody ReturnBookResponseWeb response) {
        return responseService.returnBookResponse(response);
    }
}
