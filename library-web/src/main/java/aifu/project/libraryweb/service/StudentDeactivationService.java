package aifu.project.libraryweb.service;

import aifu.project.common_domain.dto.action_dto.DeactivationStats;
import aifu.project.common_domain.entity.Student;
import aifu.project.libraryweb.repository.BookingRepository;
import aifu.project.libraryweb.repository.StudentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class StudentDeactivationService {

    private final StudentRepository studentRepository;
    private final BookingRepository bookingRepository;

    @Transactional
    public DeactivationStats deactivateStudentsFromExcel(InputStream inputStream) {
        // 1-QADAM: Exceldan faqat pasport kodlari ro'yxatini o'qib olamiz
        Set<String> passportCodesFromExcel = readPassportCodesFromExcel(inputStream);

        if (passportCodesFromExcel.isEmpty()) {
            return new DeactivationStats(0, List.of("Excel faylda pasport kodlari topilmadi."));
        }
        log.info("{} ta talabani deaktivatsiya qilish uchun so'rov keldi.", passportCodesFromExcel.size());

        // 2-QADAM: Bu pasport kodlariga ega bo'lgan talabalarni bazadan bitta so'rov bilan topamiz
        List<Student> studentsToDeactivate = studentRepository.findByPassportCodeInAndIsDeletedFalse(passportCodesFromExcel);
        Set<Long> studentIds = studentsToDeactivate.stream().map(Student::getId).collect(Collectors.toSet());

        // 3-QADAM: Topilgan talabalarning qarzdorligini bitta so'rov bilan tekshiramiz
        Set<Long> studentsWithDebt = bookingRepository.findStudentIdsWithActiveBookings(studentIds);

        List<Student> studentsToSave = new ArrayList<>();
        List<String> errors = new ArrayList<>();
        int successCount = 0;

        // 4-QADAM: Har bir talabani tekshirib, qarori bor-yo'qligiga qarab ish tutamiz
        for (Student student : studentsToDeactivate) {
            if (studentsWithDebt.contains(student.getId())) {
                // Agar talabaning qarzi bo'lsa, uni o'chirmaymiz va xatolar ro'yxatiga qo'shamiz
                String errorMsg = String.format("Talaba (ID: %d, F.I.Sh: %s %s) kitobdan qarzdorligi borligi uchun o'chirilmadi.",
                        student.getId(), student.getSurname(), student.getName());
                errors.add(errorMsg);
            } else {
                // Agar qarzi bo'lmasa, uni "yumshoq o'chiramiz"
                student.setDeleted(true);
                student.setActive(false);
                studentsToSave.add(student);
                successCount++;
            }
        }

        // 5-QADAM: Faqat o'chirilishi kerak bo'lgan talabalarni bitta so'rov bilan yangilaymiz
        if (!studentsToSave.isEmpty()) {
            studentRepository.saveAll(studentsToSave);
            log.info("{} ta talaba muvaffaqiyatli deaktivatsiya qilindi.", successCount);
        }

        return new DeactivationStats(successCount, errors);
    }

    // Exceldan faqat birinchi ustunni (pasport kodini) o'qiydigan yordamchi metod
    private Set<String> readPassportCodesFromExcel(InputStream inputStream) {
        try (Workbook workbook = new XSSFWorkbook(inputStream)) {
            Sheet sheet = workbook.getSheetAt(0);
            Set<String> passportCodes = new HashSet<>();
            for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);
                if (row != null && row.getCell(0) != null) {
                    String passportCode = new DataFormatter().formatCellValue(row.getCell(0)).trim();
                    if (!passportCode.isBlank()) {
                        passportCodes.add(passportCode);
                    }
                }
            }
            return passportCodes;
        } catch (Exception e) {
            log.error("Bitiruvchilar Excel faylini o'qishda xatolik.", e);
            throw new RuntimeException("Excel faylni o'qishda xatolik yuz berdi.");
        }
    }
}