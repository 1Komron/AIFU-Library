package aifu.project.libraryweb.controller;

import aifu.project.common_domain.payload.ResponseMessage;
import aifu.project.libraryweb.service.StudentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/student")
public class StudentController {
    private final StudentService studentService;

    @GetMapping
    public ResponseEntity<ResponseMessage> getStudents(@RequestParam(defaultValue = "1") int pageNumber,
                                                    @RequestParam(defaultValue = "10") int size) {
        return studentService.getStudentList(pageNumber, size);
    }

    @GetMapping("/search")
    public ResponseEntity<ResponseMessage> getStudents(@RequestParam(defaultValue = "1") int pageNumber,
                                                    @RequestParam(defaultValue = "10") int size,
                                                    @RequestParam(required = false) Long id,
                                                    @RequestParam(required = false) String phone,
                                                    @RequestParam(defaultValue = "id") String sortBy,
                                                    @RequestParam(defaultValue = "asc") String sortDir) {
        return studentService.getSearchStudentList(pageNumber, size, id, phone, sortBy, sortDir);
    }

    @GetMapping("/inactive")
    public ResponseEntity<ResponseMessage> getStudentsByStatus(@RequestParam(defaultValue = "1") int pageNumber,
                                                            @RequestParam(defaultValue = "10") int size) {
        return studentService.getStudentsByStatus(pageNumber, size);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ResponseMessage> getStudent(@PathVariable String id) {
        return studentService.getStudent(id);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ResponseMessage> deleteStudent(@PathVariable Long id) {
        return studentService.deleteStudent(id);
    }
}