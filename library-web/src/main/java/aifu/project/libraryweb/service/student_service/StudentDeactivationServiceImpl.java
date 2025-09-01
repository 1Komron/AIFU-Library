package aifu.project.libraryweb.service.student_service;


import aifu.project.common_domain.dto.student_dto.DeactivationResultDTO;
import aifu.project.common_domain.dto.student_dto.DebtorInfoDTO;
import aifu.project.common_domain.dto.student_dto.FileDownloadDTO;
import aifu.project.common_domain.dto.student_dto.ImportErrorDTO;
import aifu.project.common_domain.entity.Student;
import aifu.project.common_domain.exceptions.FileValidationException;
import aifu.project.common_domain.exceptions.ReportGenerationException;
import aifu.project.common_domain.exceptions.ReportNotFoundException;
import aifu.project.libraryweb.repository.BookingRepository;
import aifu.project.libraryweb.repository.StudentRepository;
import aifu.project.libraryweb.service.PassportHasher;
import aifu.project.libraryweb.service.student_service.utel_service.DeactivationJobStorage;
import aifu.project.libraryweb.service.student_service.utel_service.StudentExcelReaderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
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
    private final ImportErrorReportExcelService importErrorReportExcelService;
    private final StudentExcelReaderService studentExcelReaderService;
    private final DeactivationJobStorage deactivationJobStorage;

    @Override
    @Transactional
    public DeactivationResultDTO startDeactivationProcess(MultipartFile file) throws IOException {
        if (file == null || file.isEmpty()) {
            log.warn("Deaktivatsiya so'rovi bo'sh fayl bilan yuborildi.");
            throw new FileValidationException("Fayl bo'sh bo'lishi mumkin emas.");
        }

        UUID jobId = UUID.randomUUID();
        log.info("Deaktivatsiya jarayoni boshlandi. Job ID: {}", jobId);

        List<Student> studentsFromExcel = studentExcelReaderService.readFullStudentDataFromExcel(file.getInputStream());

        if (studentsFromExcel.isEmpty()) {
            log.warn("Job ID {}: Deaktivatsiya to'xtatildi, chunki Excel faylda ma'lumot topilmadi.", jobId);
            return new DeactivationResultDTO(jobId, 0, 0, 0, null, null);
        }
        log.info("Job ID {}: Exceldan deaktivatsiya uchun {} ta yozuv o'qildi.", jobId, studentsFromExcel.size());

        Set<String> hashedPassports = studentsFromExcel.stream()
                .map(s -> passportHasher.hash(s.getPassportCode()))
                .collect(Collectors.toSet());

        List<Student> allFoundStudentsInDb = studentRepository.findByPassportCodeInAndIsDeletedFalse(hashedPassports);
        Map<String, Student> dbStudentMap = allFoundStudentsInDb.stream().collect(Collectors.toMap(Student::getPassportCode, s -> s));

        Set<Long> foundStudentIds = allFoundStudentsInDb.stream().map(Student::getId).collect(Collectors.toSet());
        Set<Long> debtorStudentIds = foundStudentIds.isEmpty() ? Collections.emptySet() : bookingRepository.findStudentIdsWithActiveBookings(foundStudentIds);
        log.info("Job ID {}: {} ta topilgan talaba ichidan {} tasi qarzdor deb topildi.", jobId, foundStudentIds.size(), debtorStudentIds.size());

        List<Student> studentsToDeactivate = new ArrayList<>();
        List<DebtorInfoDTO> debtors = new ArrayList<>();
        List<ImportErrorDTO> notFoundRecords = new ArrayList<>();

        for (Student studentFromExcel : studentsFromExcel) {
            String hashedPassport = passportHasher.hash(studentFromExcel.getPassportCode());
            if (dbStudentMap.containsKey(hashedPassport)) {
                Student studentInDb = dbStudentMap.get(hashedPassport);
                if (debtorStudentIds.contains(studentInDb.getId())) {
                    debtors.add(new DebtorInfoDTO(studentInDb.getId(), studentInDb.getName(), studentInDb.getSurname(), studentInDb.getFaculty()));
                } else {
                    studentsToDeactivate.add(studentInDb);
                }
            } else {
                notFoundRecords.add(new ImportErrorDTO(studentFromExcel.getName(), studentFromExcel.getSurname(), studentFromExcel.getDegree(),
                        studentFromExcel.getFaculty(), studentFromExcel.getAdmissionTime(), studentFromExcel.getGraduationTime(),
                        "Talaba bazada topilmadi yoki allaqachon o'chirilgan."));
            }
        }

        if (!studentsToDeactivate.isEmpty()) {
            studentsToDeactivate.forEach(s -> {
                s.setDeleted(true);
                s.setActive(false);
            });
            studentRepository.saveAll(studentsToDeactivate);
            log.info("Job ID {}: {} ta talaba muvaffaqiyatli deaktivatsiya qilindi.", jobId, studentsToDeactivate.size());
        }

        deactivationJobStorage.saveReports(jobId, debtors, notFoundRecords);

        String debtorsUrl = debtors.isEmpty() ? null : "/api/super-admin/students/lifecycle/report/debtors/" + jobId;
        String notFoundUrl = notFoundRecords.isEmpty() ? null : "/api/super-admin/students/lifecycle/report/not-found/" + jobId;

        log.info("Deaktivatsiya jarayoni yakunlandi. Job ID: {}. Muvaffaqiyatli: {}, Qarzdorlar: {}, Topilmaganlar: {}",
                jobId, studentsToDeactivate.size(), debtors.size(), notFoundRecords.size());

        return new DeactivationResultDTO(jobId, studentsToDeactivate.size(), debtors.size(), notFoundRecords.size(), debtorsUrl, notFoundUrl);
    }

    @Override
    public FileDownloadDTO getDebtorsReport(UUID jobId) {
        log.info("{} ID'li deaktivatsiya jarayonining qarzdorlar hisoboti so'raldi.", jobId);
        DeactivationJobStorage.ReportData reports = deactivationJobStorage.getReports(jobId);

        if (reports == null || reports.getDebtors().isEmpty()) {
            throw new ReportNotFoundException(jobId, "Qarzdorlar");
        }

        try {
            byte[] excelFile = debtorReportExcelService.genereateDebtorReport(reports.getDebtors());
            String fileName = "qarzdor_talabalar_" + LocalDate.now().format(DateTimeFormatter.ofPattern("dd-MM-yyyy")) + ".xlsx";
            log.info("Job ID {} uchun '{}' nomli qarzdorlar hisoboti muvaffaqiyatli yaratildi.", jobId, fileName);
            return new FileDownloadDTO(fileName, new ByteArrayResource(excelFile));
        } catch (Exception e) {
            throw new ReportGenerationException(jobId, "Qarzdorlar", e);
        }
    }

    @Override
    public FileDownloadDTO getNotFoundReport(UUID jobId) {
        log.info("{} ID'li deaktivatsiya jarayonining topilmaganlar hisoboti so'raldi.", jobId);
        DeactivationJobStorage.ReportData reports = deactivationJobStorage.getReports(jobId);

        if (reports == null || reports.getNotFoundRecords().isEmpty()) {
            throw new ReportNotFoundException(jobId, "Topilmagan talabalar");
        }

        try {
            byte[] excelFile = importErrorReportExcelService.generateStudentImportErrorReport(reports.getNotFoundRecords());
            String fileName = "topilmagan_talabalar_" + LocalDate.now().format(DateTimeFormatter.ofPattern("dd-MM-yyyy")) + ".xlsx";
            log.info("Job ID {} uchun '{}' nomli topilmaganlar hisoboti muvaffaqiyatli yaratildi.", jobId, fileName);
            return new FileDownloadDTO(fileName, new ByteArrayResource(excelFile));
        } catch (IOException e) {
            throw new ReportGenerationException(jobId, "Topilmagan talabalar", e);
        }
    }
}