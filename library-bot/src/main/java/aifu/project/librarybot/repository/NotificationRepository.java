package aifu.project.librarybot.repository;

import aifu.project.common_domain.entity.BookCopy;
import aifu.project.common_domain.entity.Notification;
import aifu.project.common_domain.entity.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {
    boolean existsByStudentAndBookCopy(Student student, BookCopy bookCopy);
}
