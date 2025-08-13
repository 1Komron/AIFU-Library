package aifu.project.libraryweb.controller.admin_controller;

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
@RequestMapping("/api/admin/students")
public class StudentController {
    private final StudentService studentService;

    @GetMapping()
    @Operation(summary = "Studentlar ro'yxatini olish",
            description = """
                    Parametrlar:
                    - filter: id, cardNumber, fullName (Ism va Familiya)
                    - pageNumber: Sahifa raqami (default: 1)
                    - cardNumber: Student kartasi raqami
                    - sortBy: Tartiblash uchun field (default: id)
                    - sortDir: Tartiblash yo'nalishi (default: asc) 'asc' yoki 'desc'
                    
                    Eslatma: id doimo son korinishida bolishi kerak. Ism va Familiya faqat 2 ta so'zdan iborat bo'lishi kerak.
                    """)
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Studentlar qidiruvi muvaffaqiyatli amalga oshirildi"),
            @ApiResponse(responseCode = "400", description = "Noto'g'ri filter yoki query qiymat"),
    })
    public ResponseEntity<ResponseMessage> getAll(@RequestParam(required = false) String field,
                                                  @RequestParam(required = false) String query,
                                                  @RequestParam(defaultValue = "all") String status,
                                                  @RequestParam(defaultValue = "1") int pageNumber,
                                                  @RequestParam(defaultValue = "10") int size,
                                                  @RequestParam(defaultValue = "asc") String sortDirection) {

        return studentService.getAll(field, query, status, pageNumber, size, sortDirection);
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

    @GetMapping("/card/{cardNumber}")
    @Operation(summary = "Studentni ma'lumotlarini CardNumber orqali olish")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Student ma'lumotlari muvaffaqiyatli olindi"),
            @ApiResponse(responseCode = "404", description = "Student topilmadi"),
    })
    public ResponseEntity<ResponseMessage> getStudentByCardNumber(@PathVariable String cardNumber) {
        return studentService.getStudentByCardNumber(cardNumber);
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

    @PatchMapping("/{id}")
    @Operation(summary = "Student cardNumber ni yangilash",
            description = "Student cardNumber ni yangilash uchun yangi cardNumber yuboriladi.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Student cardNumber muvaffaqiyatli yangilandi"),
            @ApiResponse(responseCode = "404", description = "Student topilmadi"),
            @ApiResponse(responseCode = "400", description = "CardNumber allaqachon mavjud")
    })
    public ResponseEntity<ResponseMessage> updateCardNumber(@PathVariable Long id,
                                                            @RequestParam String cardNumber) {
        return studentService.updateCardNumber(id, cardNumber);
    }
}