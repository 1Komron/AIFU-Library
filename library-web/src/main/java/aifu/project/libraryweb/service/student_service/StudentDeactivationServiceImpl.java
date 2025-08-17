package aifu.project.libraryweb.service.student_service;

import aifu.project.common_domain.dto.action_dto.DeactivationStats;
import aifu.project.common_domain.dto.student_dto.DebtorInfoDTO;
import aifu.project.common_domain.entity.Student;
import aifu.project.common_domain.exceptions.StudentImportException;
import aifu.project.libraryweb.repository.BookingRepository;
import aifu.project.libraryweb.repository.StudentRepository;
import aifu.project.libraryweb.service.PassportHasher;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
@Slf4j
public class StudentDeactivationServiceImpl implements StudentDeactivationService {

    private final StudentRepository studentRepository;
    private final PassportHasher passportHasher;
    private final BookingRepository bookingRepository;
    private final DebtorReportExcelService debtorReportExcelService;


    @Override
    @Transactional
    public DeactivationStats deactivateStudents(InputStream inputStream) {
        Set<String> plainPassportCodesFromExcel = readPassportCodesFromExcel(inputStream);
        if (plainPassportCodesFromExcel.isEmpty()) {
            return new DeactivationStats(0, Collections.emptyList(), List.of("Excel faylda pasport kodlari topilmadi."));
        }
        Map<String, String> hashedToPlainMap = plainPassportCodesFromExcel.stream()
                .collect(Collectors.toMap(passportHasher::hash, plain -> plain, (p1, p2) -> p1));
        List<Student> allFoundStudents = studentRepository.findByPassportCodeInAndIsDeletedFalse(hashedToPlainMap.keySet());

        if (allFoundStudents.isEmpty()) {
            List<String> notFoundErrors = plainPassportCodesFromExcel.stream()
                    .map(plain -> "Talaba (Pasport: " + plain + ") ma'lumotlar bazasida topilmadi.")
                    .collect(Collectors.toList());
            return new DeactivationStats(0, Collections.emptyList(), notFoundErrors);
        }


        Set<Long> foundStudentIds = allFoundStudents.stream().map(Student::getId).collect(Collectors.toSet());
        Set<Long> debtorStudentIds = bookingRepository.findStudentIdsWithActiveBookings(foundStudentIds);
        log.info("{} ta topilgan talaba ichidan {} tasi qarzdor deb topildi.", foundStudentIds.size(), debtorStudentIds.size());


        List<Student> studentsToDeactivate = new ArrayList<>();
        List<DebtorInfoDTO> debtors = new ArrayList<>();

        for (Student student : allFoundStudents) {
            if (debtorStudentIds.contains(student.getId())) {
                debtors.add(new DebtorInfoDTO(student.getId(), student.getName(), student.getSurname(), student.getCardNumber()));
            } else {
                studentsToDeactivate.add(student);
            }
        }

        List<String> notFoundOrOtherErrors = new ArrayList<>();
        Set<String> foundHashedPassports = allFoundStudents.stream().map(Student::getPassportCode).collect(Collectors.toSet());
        hashedToPlainMap.forEach((hashed, plain) -> {
            if (!foundHashedPassports.contains(hashed)) {
                notFoundOrOtherErrors.add("Talaba (Pasport: " + plain + ") ma'lumotlar bazasida topilmadi.");
            }
        });

        if (!studentsToDeactivate.isEmpty()) {
            studentsToDeactivate.forEach(student -> {
                student.setDeleted(true);
                student.setActive(false);
            });
            studentRepository.saveAll(studentsToDeactivate);
            log.info("{} ta talaba muvaffaqiyatli deaktivatsiya qilindi.", studentsToDeactivate.size());
        }

        DeactivationStats stats = new DeactivationStats(studentsToDeactivate.size(), debtors, notFoundOrOtherErrors);

        if (!debtors.isEmpty()) {
            try {
                byte[] excelFile = debtorReportExcelService.genereateDebtorReport(debtors);

                String fileName = "qarzdorlar_" + LocalDate.now().format(DateTimeFormatter.ofPattern("dd-MM-yyyy")) + ".xlsx";

                stats.setReportFileName(fileName);
                stats.setReportFileBase64(Base64.getEncoder().encodeToString(excelFile));

                log.info("{} nomli qarzdorlar hisoboti muvaffaqiyatli generatsiya qilindi.", fileName);

            } catch (IOException e) {
                log.error("Qarzdorlar hisobotini Excelga yozishda xatolik yuz berdi!", e);
                stats.getNotFoundOrOtherErrors().add("Excel hisobotini generatsiya qilishda kutilmagan xatolik yuz berdi.");
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }

        return stats;
    }

    private Set<String> readPassportCodesFromExcel(InputStream inputStream) {
        try (Workbook workbook = new XSSFWorkbook(inputStream)) {

            Sheet sheet = workbook.getSheetAt(0);
            DataFormatter dataFormatter = new DataFormatter();
            Row headerRow = sheet.getRow(0);
            if (headerRow == null) {
                throw new StudentImportException("Excel fayl formati noto'g'ri: sarlavha qatori (birinchi qator) mavjud emas.");
            }
            int passportColumnIndex = -1;
            for (int i = 0; i < headerRow.getLastCellNum(); i++) {
                Cell cell = headerRow.getCell(i);
                if (cell == null) {
                    continue;
                }
                String headerValue = dataFormatter.formatCellValue(cell).trim().toLowerCase();
                if (headerValue.contains("passport") || headerValue.contains("pasport")) {
                    passportColumnIndex = cell.getColumnIndex();
                    break;
                }
            }
            if (passportColumnIndex == -1) {
                throw new StudentImportException("Pasport kodi ustuni topilmadi. Iltimos, Excel fayldagi ustun nomida " +
                                                 "'passport' yoki 'pasport' so'zi borligiga ishonch hosil qiling.");
            }
            Set<String> passportCodes = new HashSet<>();
            for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);
                if (row != null) {
                    Cell cell = row.getCell(passportColumnIndex);
                    if (cell != null) {
                        String passportCode = dataFormatter.formatCellValue(cell).trim();
                        if (!passportCode.isBlank()) {
                            passportCodes.add(passportCode);
                        }
                    }
                }
            }
            return passportCodes;

        } catch (StudentImportException e) {
            log.warn("Talabalarni import qilishda mantiqiy xato: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("Excel faylni o'qishda texnik xatolik yuz berdi.", e);
            throw new StudentImportException("Excel faylni o'qishda kutilmagan texnik xatolik yuz berdi. Fayl formati to'g'riligini tekshiring.", e);
        }
    }
}