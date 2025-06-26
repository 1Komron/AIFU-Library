package aifu.project.libraryweb.controller;

import aifu.project.common_domain.payload.ResponseMessage;
import aifu.project.libraryweb.service.UserService;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/user")
public class UserController {
    private final UserService userService;

    @GetMapping
    public ResponseEntity<ResponseMessage> getUsers(@RequestParam(defaultValue = "1") int pageNumber,
                                                    @RequestParam(defaultValue = "10") int size) {
        return userService.getUserList(pageNumber, size);
    }

    @GetMapping("/inactive")
    public ResponseEntity<ResponseMessage> getUsersByStatus(@RequestParam(defaultValue = "1") int pageNumber,
                                                            @RequestParam(defaultValue = "10") int size) {
        return userService.getUsersByStatus(pageNumber, size);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ResponseMessage> getUser(@PathVariable String id) {
        return userService.getUser(id);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ResponseMessage> deleteUser(@PathVariable Long id) {
        return userService.deleteUser(id);
    }
}