package aifu.project.libraryweb.service.student_service;

import aifu.project.common_domain.dto.student_dto.CreateStudentDTO;
import aifu.project.common_domain.entity.Student;
import aifu.project.common_domain.exceptions.CardNumberAlreadyExistsException;
import aifu.project.common_domain.exceptions.UserAlreadyExistsException;
import aifu.project.common_domain.exceptions.UserDeletionException;
import aifu.project.common_domain.exceptions.UserNotFoundException;
import aifu.project.common_domain.dto.ResponseMessage;
import aifu.project.common_domain.dto.student_dto.StudentShortDTO;
import aifu.project.common_domain.dto.student_dto.StudentSummaryDTO;
import aifu.project.libraryweb.repository.StudentRepository;
import aifu.project.libraryweb.service.PassportHasher;
import aifu.project.libraryweb.service.booking_serivce.BookingService;
import aifu.project.libraryweb.utils.Util;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.*;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class StudentServiceImpl implements StudentService {
    private final StudentRepository studentRepository;
    private final BookingService bookingService;
    private final PassportHasher passportHasher;

    private static final String DEFAULT = "default";

    public long countStudents() {
        return studentRepository.getStudentsCount();
    }

    @Override
    public ResponseEntity<ResponseMessage> createStudent(CreateStudentDTO createStudentDTO) {
        log.info("Student yaratish jarayoni...");
        log.info("{}", createStudentDTO.toString());

        String passport = checkPassport(createStudentDTO);
        String cardNumber = checkCardNumber(createStudentDTO);

        Student student = CreateStudentDTO.toEntity(createStudentDTO, passport, cardNumber);
        studentRepository.save(student);

        StudentSummaryDTO response = StudentSummaryDTO.toDTO(student);

        log.info("Student muvaffaqiyatli yaratildi. Student: {}", student);

        return ResponseEntity.status(HttpStatus.CREATED).body(new ResponseMessage(true, "Student muvaffaqiyatli qo'shildi", response));
    }

    private String checkCardNumber(CreateStudentDTO createStudentDTO) {
        log.info("Student cardNumber tekshirilmoqda...");

        String cardNumber = createStudentDTO.cardNumber();
        if (studentRepository.existsByCardNumber(cardNumber)) {
            throw new CardNumberAlreadyExistsException("Card number already exists.");
        }

        log.info("Student cardNumber bazada mavjud emas. Davom etilmoqda...");

        return cardNumber;
    }

    private String checkPassport(CreateStudentDTO createStudentDTO) {
        log.info("Student passporti tekshirilmoqda...");

        String passportSeries = createStudentDTO.passportSeries().toUpperCase().trim();
        String passportNumber = createStudentDTO.passportNumber().trim();
        String passportHash = passportHasher.hash(passportSeries + passportNumber);

        if (studentRepository.existsByPassportCode(passportHash)) {
            throw new UserAlreadyExistsException("Student passporti bazada mavjud: " + passportSeries + passportNumber);
        }

        log.info("Student passporti bazada mavjud emas. Davom etilmoqda...");

        return passportHash;
    }

    @Override
    public ResponseEntity<ResponseMessage> getAll(String field,
                                                  String query,
                                                  String status,
                                                  int pageNumber,
                                                  int size,
                                                  String sortDirection) {
        log.info("Studentlar ro'yxatini olish jarayoni boshlandi...");
        log.info("Kiritilgan parametrlar: field={}, query={}, status={}, pageNumber={}, size={}, sortDirection={}",
                field, query, status, pageNumber, size, sortDirection);

        field = field == null ? DEFAULT : field;
        if (!field.equals(DEFAULT) && query == null) {
            throw new IllegalArgumentException("Query qiymati: null. Field qiymati: %s".formatted(field));
        }

        Sort.Direction direction = sortDirection.equalsIgnoreCase("asc") ? Sort.Direction.ASC : Sort.Direction.DESC;
        Pageable pageable = PageRequest.of(--pageNumber, size, Sort.by(direction, "id"));

        List<Boolean> statusList = switch (status) {
            case "active" -> List.of(true);
            case "inactive" -> List.of(false);
            default -> List.of(true, false);
        };

        Page<Student> studentPage = switch (field) {
            case "id" -> studentRepository.findByIdAndIsDeletedFalse(Long.parseLong(query), pageable, statusList);
            case "cardNumber" -> studentRepository.findByCardNumberAndIsDeletedFalse(query, pageable, statusList);
            case "fullName" -> {
                String[] parts = query.trim().split("~");

                String first = parts[0].isBlank() ? null : "%" + parts[0].toLowerCase() + "%";
                String second = (parts.length == 2) ? "%" + parts[1].toLowerCase() + "%" : null;

                yield studentRepository.findBySurnameAndName(first, second, pageable, statusList);
            }
            case DEFAULT -> studentRepository.findByIsDeletedFalse(pageable, statusList);
            default -> throw new IllegalArgumentException("Invalid field: " + field);
        };

        List<Student> content = studentPage.getContent();

        log.info("Studentlar ro'yxati: field={}, query={}, pageNumber={}, size={}, sortDirection={}",
                field, query, pageNumber + 1, size, sortDirection);

        log.info("Studentlar ro'yxati: {}", content.stream().map(Student::getId).toList());

        Map<String, Object> map = Util.getPageInfo(studentPage);
        map.put("data", getStudentShortDTO(content));

        return ResponseEntity.ok(new ResponseMessage(true, "Student list", map));
    }

    @Override
    public ResponseEntity<ResponseMessage> getStudent(Long id) {
        log.info("ID bo'yicha student ma'lumotlarini olish jarayoni boshlandi... ID: {}", id);

        Student student = findStudent(id);

        StudentSummaryDTO dto = StudentSummaryDTO.toDTO(student);

        log.info("Stundent ID bo'yicha topildi. Student: {}", student);

        return ResponseEntity.ok(new ResponseMessage(true, "Student malumotlari", dto));
    }

    @Override
    public ResponseEntity<ResponseMessage> getStudentByCardNumber(String cardNumber) {
        log.info("CardNumber bo'yicha student ma'lumotlarini olish jarayoni boshlandi... CardNumber: {}", cardNumber);

        Student student = findByCardNumber(cardNumber);

        StudentSummaryDTO dto = StudentSummaryDTO.toDTO(student);

        log.info("Stundent CardNumber bo'yicha topildi. Student: {}", student);

        return ResponseEntity.ok(new ResponseMessage(true, "Student malumotlari", dto));
    }

    private List<StudentShortDTO> getStudentShortDTO(List<Student> students) {
        return students.stream()
                .map(user -> new StudentShortDTO(user.getId(), user.getName(),
                        user.getSurname(), user.getCardNumber(), user.getDegree(), user.isActive()))
                .toList();
    }

    @Override
    public ResponseEntity<ResponseMessage> deleteStudent(Long id) {
        log.info("ID bo'yicha studentni o'chirish jarayoni boshlandi... ID: {}", id);

        Student student = findStudent(id);

        if (bookingService.hasBookingForUser(id))
            throw new UserDeletionException("Studnetda aktiv bronlar mavjud. Student ID: " + id);

        student.setDeleted(true);
        student.setActive(false);
        student.setChatId(null);
        student.setCardNumber(null);
        studentRepository.save(student);

        log.info("ID bo'yicha student o'chirildi. Student: {}", student);

        return ResponseEntity.ok(new ResponseMessage(true, "Student muvaffaqiyatli o'chirildi", null));
    }

    @Override
    public ResponseEntity<ResponseMessage> update(Long id, Map<String, Object> updates) {
        log.info("ID bo'yicha studentni update qilish jarayoni boshlandi... ID: {}, Update qilinadigan fieldlar: {}",
                id, updates.keySet());

        Student student = findStudent(id);

        for (Map.Entry<String, Object> entry : updates.entrySet()) {
            String key = entry.getKey();
            Object value = entry.getValue();

            if (key == null) {
                throw new IllegalArgumentException("Key Null");
            }

            if (value == null) {
                throw new IllegalArgumentException("Value Null");
            }

            switch (key) {
                case "name" -> student.setName(((String) value).trim());
                case "surname" -> student.setSurname(((String) value).trim());
                case "phoneNumber" -> student.setPhoneNumber(((String) value).trim());
                case "degree" -> student.setDegree(((String) value).trim());
                case "faculty" -> student.setFaculty(((String) value).trim());
                case "cardNumber" -> {
                    String cardNumber = ((String) value).trim();

                    if (studentRepository.existsByCardNumber(cardNumber)) {
                        throw new IllegalArgumentException("Card Number mavjud: " + cardNumber);
                    }
                    student.setCardNumber(cardNumber);
                }
                case "admissionTime" -> student.setAdmissionTime(LocalDate.of((Integer) value, 8, 1));
                case "graduationTime" -> student.setGraduationTime(LocalDate.of((Integer) value, 8, 1));
                default -> throw new IllegalArgumentException("Invalid key: " + key);
            }
        }

        student = studentRepository.save(student);
        StudentSummaryDTO dto = StudentSummaryDTO.toDTO(student);

        log.info("Stundent ID bo'yicha update qilindi. \nStudent: {}\n Update qilingan fieldlar: {}", student, updates.keySet());

        return ResponseEntity.ok(new ResponseMessage(true, "Student malumotlari muvaffqiyatli yangilandi", dto));
    }

    public Student findByCardNumber(String cardNumber) {
        return studentRepository.findByCardNumberAndIsDeletedFalse(cardNumber)
                .orElseThrow(() -> new UserNotFoundException("Student CardNumber orqali topilmadi. CardNumber: " + cardNumber));
    }

    @Override
    public List<Student> getAll() {
        return studentRepository.findAllByIsDeletedFalse();
    }

    @Override
    public Student findStudent(Long id) {
        return studentRepository.findByIdAndIsDeletedFalse(id)
                .orElseThrow(() -> new UserNotFoundException("Student topilmadi. ID: " + id));
    }

    @PostConstruct
    public void init() {
        this.bookingService.setStudentService(this);
    }
}
