package aifu.project.libraryweb.repository;

import aifu.project.common_domain.entity.Student;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.Set;

@Repository
public interface StudentRepository extends JpaRepository<Student, Long> {

    @Query("select count(*) from Student s where s.isDeleted = false")
    long getStudentsCount();

    Page<Student> findByIsDeletedFalse(Pageable pageable);

    Page<Student> findByIdAndIsDeletedFalse(Long id, Pageable pageable);

    Optional<Student> findByCardNumberAndIsDeletedFalse(String cardNumber);


    @Query("select s.passportCode from Student s")
    Set<String> findAllPassportCodes();

    Optional<Student> findByIdAndIsDeletedFalse(Long userId);

    Page<Student> findByCardNumberAndIsDeletedFalse(String cardNumber, Pageable pageable);

    Page<Student> findByNameContainingIgnoreCaseAndIsDeletedFalse(String query, Pageable pageable);

    Page<Student> findByIsActiveAndIsDeletedFalse(boolean b, Pageable pageable);
}
