package aifu.project.libraryweb.service;

import aifu.project.common_domain.entity.Student;
import aifu.project.common_domain.entity.enums.Role;
import aifu.project.common_domain.exceptions.UserDeletionException;
import aifu.project.common_domain.exceptions.UserNotFoundException;
import aifu.project.common_domain.payload.ResponseMessage;
import aifu.project.common_domain.payload.StudentShortDTO;
import aifu.project.common_domain.payload.StudentSummaryDTO;
import aifu.project.libraryweb.repository.StudentRepository;
import aifu.project.libraryweb.utils.Util;
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
public class StudentService {
    private final StudentRepository studentRepository;
    private final BookingService bookingService;

    public long countStudents() {
        return studentRepository.getStudentsCount();
    }

    public ResponseEntity<ResponseMessage> getStudentList(int pageNumber, int size) {

        Pageable pageable = PageRequest.of(--pageNumber, size, Sort.by(Sort.Direction.ASC, "id"));

        Page<Student> studentPage = studentRepository.findByRoleAndIsDeletedFalse(Role.USER, pageable);

        Map<String, Object> map = Util.getPageInfo(studentPage);
        map.put("data", getStudentShortDTO(studentPage.getContent()));

        return ResponseEntity.ok(new ResponseMessage(true, "Student list", map));
    }

    public ResponseEntity<ResponseMessage> getSearchStudentList(int pageNumber,
                                                                int size,
                                                                Long id,
                                                                String phone,
                                                                String sortBy,
                                                                String sortDir) {
        Sort sort = sortDir.equalsIgnoreCase("asc") ? Sort.by(Sort.Direction.ASC, sortBy) : Sort.by(Sort.Direction.DESC, sortBy);
        Pageable pageable = PageRequest.of(--pageNumber, size, sort);

        Page<Student> studentPage;
        if (id != null) {
            studentPage = studentRepository.findByIdAndRoleAndIsDeletedFalse(id, Role.USER, pageable);
        }
//        else if (phone != null) {
//            studentPage = userRepository.findByPhoneAndRoleAndIsDeletedFalse(phone, Role.USER, pageable);
//        }
        else {
            studentPage = studentRepository.findByRoleAndIsDeletedFalse(Role.USER, pageable);
        }

        Map<String, Object> map = Util.getPageInfo(studentPage);
        map.put("data", getStudentShortDTO(studentPage.getContent()));

        return ResponseEntity.ok(new ResponseMessage(true, "Student list", map));
    }

    public ResponseEntity<ResponseMessage> getStudentsByStatus(int pageNumber, int size) {
        Pageable pageable = PageRequest.of(--pageNumber, size, Sort.by(Sort.Direction.ASC, "id"));

        Page<Student> studentPage = studentRepository.findByRoleAndIsActiveAndIsDeletedFalse(Role.USER, false, pageable);

        Map<String, Object> map = Util.getPageInfo(studentPage);
        map.put("data", getStudentShortDTO(studentPage.getContent()));

        return ResponseEntity.ok(new ResponseMessage(true, "Student list", map));
    }

    public ResponseEntity<ResponseMessage> getStudent(String id) {
        Student user = studentRepository.findById(Long.parseLong(id))
                .orElseThrow(() -> new UserNotFoundException("User not found by id:" + id));

        StudentSummaryDTO dto = new StudentSummaryDTO(
                user.getId(),
                user.getName(),
                user.getSurname(),
                user.getDegree(),
                user.getFaculty(),
                user.getChatId(),
                user.isActive()
        );

        return ResponseEntity.ok(new ResponseMessage(true, "Detailed user information", dto));
    }


    private List<StudentShortDTO> getStudentShortDTO(List<Student> students) {
        return students.stream()
                .map(user -> new StudentShortDTO(user.getId(), user.getName(),
                        user.getSurname(), user.getCardNumber(), user.isActive()))
                .toList();
    }

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
}
