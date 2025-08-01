package aifu.project.libraryweb.controller;

import aifu.project.common_domain.dto.ResponseMessage;
import aifu.project.libraryweb.service.student_service.StudentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/student")
public class StudentController {
    private final StudentService studentService;

    @GetMapping
    @Operation(summary = "Studentlar ro'yxatini olish",
            description = """
                    Parametrlar:
                    - `filter`: `active`, `inactive` yoki `all` qiymatlari qabul qilinadi.
                    - pageNumber: Sahifa raqami (default: 1)
                    - pageSize: Sahifa hajmi (default: 10)
                    - sortDirection: Tartiblash yo'nalishi (default: asc) 'asc' yoki 'desc'
                    """)
    @ApiResponse(responseCode = "200", description = "Studentlar ro'yxati muvaffaqiyatli olindi")
    public ResponseEntity<ResponseMessage> getStudents(@RequestParam(required = false, defaultValue = "all") String filter,
                                                       @RequestParam(defaultValue = "1") int pageNumber,
                                                       @RequestParam(defaultValue = "10") int size,
                                                       @RequestParam(required = false, defaultValue = "asc") String sortDirection) {
        return studentService.getStudentList(filter, pageNumber, size, sortDirection);
    }

    @GetMapping("/search")
    @Operation(summary = "Studentlarni qidirish",
            description = """
                    Parametrlar:
                    - filter: id, cardNumber, name
                    - pageNumber: Sahifa raqami (default: 1)
                    - cardNumber: Student kartasi raqami
                    - sortBy: Tartiblash uchun field (default: id)
                    - sortDir: Tartiblash yo'nalishi (default: asc) 'asc' yoki 'desc'
                    
                    Eslatma: id doimo son korinishida bolishi kerak.
                    """)
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Studentlar qidiruvi muvaffaqiyatli amalga oshirildi"),
            @ApiResponse(responseCode = "400", description = "Noto'g'ri filter yoki query qiymat"),
    })
    public ResponseEntity<ResponseMessage> search(@RequestParam String filter,
                                                  @RequestParam String query,
                                                  @RequestParam(defaultValue = "1") int pageNumber,
                                                  @RequestParam(defaultValue = "10") int size,
                                                  @RequestParam(defaultValue = "asc") String sortDirection) {

        return studentService.getSearchStudentList(filter, query, pageNumber, size, sortDirection);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Studentni ma'lumotlarini ID orqali olish")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Student ma'lumotlari muvaffaqiyatli olindi"),
            @ApiResponse(responseCode = "404", description = "Student topilmadi"),
    })
    public ResponseEntity<ResponseMessage> getStudent(@PathVariable String id) {
        return studentService.getStudent(id);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Studentni o'chirish")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Student o'chirildi"),
            @ApiResponse(responseCode = "400", description = "Studentda active booking mavjud"),
            @ApiResponse(responseCode = "404", description = "Student topilmadi"),
    })
    public ResponseEntity<ResponseMessage> deleteStudent(@PathVariable Long id) {
        return studentService.deleteStudent(id);
    }
}