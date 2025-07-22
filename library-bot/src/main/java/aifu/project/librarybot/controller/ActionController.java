package aifu.project.librarybot.controller;

import aifu.project.common_domain.payload.*;
import aifu.project.librarybot.service.ActionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/action")
@RequiredArgsConstructor
public class ActionController {
    private final ActionService actionService;

    @PostMapping("/book/borrow")
    public ResponseEntity<ResponseMessage> borrowBookResponse(@RequestBody BorrowBookRequest borrowBookRequest) {
        return actionService.borrowBookResponse(borrowBookRequest);
    }

    @PostMapping("/book/extend")
    public ResponseEntity<ResponseMessage> extendBookResponse(@RequestBody ExtendBookRequest extendBookRequest) {
        return actionService.extendBookResponse(extendBookRequest);
    }

    @PostMapping("/book/return")
    public ResponseEntity<ResponseMessage> returnBookResponse(@RequestBody ReturnBookRequest response) {
        return actionService.returnBookResponse(response);
    }
}
