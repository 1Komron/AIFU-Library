package aifu.project.librarybot.repository;

import aifu.project.common_domain.entity.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface StudentRepository extends JpaRepository<Student, Long> {

    boolean existsByChatIdAndIsActiveTrueAndIsDeletedFalse(Long chatId);

    Optional<Student> findByChatIdAndIsDeletedFalse(Long chatId);

    Student findByIsDeletedFalseAndPassportCode(String passportCode);
}
