package aifu.project.libraryweb.service.student_service;

import aifu.project.common_domain.entity.Student;
import aifu.project.common_domain.exceptions.UserDeletionException;
import aifu.project.common_domain.exceptions.UserNotFoundException;
import aifu.project.common_domain.dto.ResponseMessage;
import aifu.project.common_domain.dto.student_dto.StudentShortDTO;
import aifu.project.common_domain.dto.student_dto.StudentSummaryDTO;
import aifu.project.libraryweb.repository.StudentRepository;
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

import java.util.List;
import java.util.Map;

import static aifu.project.common_domain.exceptions.UserNotFoundException.NOT_FOUND_BY_CHAT_ID;

@Slf4j
@Service
@RequiredArgsConstructor
public class StudentServiceImpl implements StudentService {
    private final StudentRepository studentRepository;
    private final BookingService bookingService;

    public long countStudents() {
        return studentRepository.getStudentsCount();
    }

    @Override
    public ResponseEntity<ResponseMessage> getStudentList(String filter, int pageNumber, int size, String sortDirection) {
        Sort.Direction direction = sortDirection.equalsIgnoreCase("asc") ? Sort.Direction.ASC : Sort.Direction.DESC;
        Pageable pageable = PageRequest.of(--pageNumber, size, Sort.by(direction, "id"));


        Page<Student> studentPage = switch (filter.toLowerCase()) {
            case "active" -> studentRepository.findByIsActiveAndIsDeletedFalse(true, pageable);
            case "inactive" -> studentRepository.findByIsActiveAndIsDeletedFalse(false, pageable);
            default -> studentRepository.findByIsDeletedFalse(pageable);
        };

        Map<String, Object> map = Util.getPageInfo(studentPage);
        map.put("data", getStudentShortDTO(studentPage.getContent()));

        return ResponseEntity.ok(new ResponseMessage(true, "Student list", map));
    }

    @Override
    public ResponseEntity<ResponseMessage> getSearchStudentList(String filter,
                                                                String query,
                                                                int pageNumber,
                                                                int size,
                                                                String sortDirection) {
        Sort.Direction direction = sortDirection.equalsIgnoreCase("asc") ? Sort.Direction.ASC : Sort.Direction.DESC;
        Pageable pageable = PageRequest.of(--pageNumber, size, Sort.by(direction, "id"));

        Page<Student> studentPage = switch (filter) {
            case "id" -> studentRepository.findByIdAndIsDeletedFalse(Long.parseLong(query), pageable);
            case "cardNumber" -> studentRepository.findByCardNumberAndIsDeletedFalse(query, pageable);
            case "name" -> studentRepository.findByNameContainingIgnoreCaseAndIsDeletedFalse(query, pageable);
            default -> throw new IllegalArgumentException("Invalid filter: " + filter);
        };

        Map<String, Object> map = Util.getPageInfo(studentPage);
        map.put("data", getStudentShortDTO(studentPage.getContent()));

        return ResponseEntity.ok(new ResponseMessage(true, "Student list", map));
    }

    @Override
    public ResponseEntity<ResponseMessage> getStudent(String id) {
        Student student = studentRepository.findById(Long.parseLong(id))
                .orElseThrow(() -> new UserNotFoundException("User not found by id:" + id));

        StudentSummaryDTO dto = new StudentSummaryDTO(
                student.getId(),
                student.getName(),
                student.getSurname(),
                student.getDegree(),
                student.getFaculty(),
                student.getCardNumber(),
                student.getChatId(),
                student.isActive()
        );

        return ResponseEntity.ok(new ResponseMessage(true, "Detailed user information", dto));
    }


    private List<StudentShortDTO> getStudentShortDTO(List<Student> students) {
        return students.stream()
                .map(user -> new StudentShortDTO(user.getId(), user.getName(),
                        user.getSurname(), user.getCardNumber(), user.isActive()))
                .toList();
    }

    @Override
    public ResponseEntity<ResponseMessage> deleteStudent(Long userId) {
        Student student = studentRepository.findByIdAndIsDeletedFalse(userId)
                .orElseThrow(() -> new UserNotFoundException(NOT_FOUND_BY_CHAT_ID + userId));

        if (bookingService.hasBookingForUser(userId))
            throw new UserDeletionException("The user cannot be deleted because he has active book reservations.");

        student.setDeleted(true);
        student.setActive(false);
        student.setChatId(null);
        studentRepository.save(student);

        return ResponseEntity.ok(new ResponseMessage(true, "User successfully deleted", null));
    }

    public Student findByCardNumber(String cardNumber) {
        return studentRepository.findByCardNumberAndIsActiveTrueAndIsDeletedFalse(cardNumber)
                .orElseThrow(() -> new UserNotFoundException("User not found by card number: " + cardNumber));
    }

    @PostConstruct
    public void init() {
        this.bookingService.setStudentService(this);
    }
}
