package aifu.project.libraryweb.repository;

import aifu.project.common_domain.entity.Student;
import aifu.project.common_domain.entity.enums.Role;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface StudentRepository extends JpaRepository<Student, Long> {

    @Query("select count(*) from Student s where s.role = 'USER' and s.isDeleted = false")
    long getStudentsCount();

    Page<Student> findByRoleAndIsDeletedFalse(Role role, Pageable pageable);

    Page<Student> findByRoleAndIsActiveAndIsDeletedFalse(Role role, boolean active, Pageable pageable);

    Page<Student> findByIdAndRoleAndIsDeletedFalse(Long id, Role role, Pageable pageable);

//    Page<Student> findByPhoneAndRoleAndIsDeletedFalse(String phone, Role role, Pageable pageable);

    Optional<Student> findByCardNumberAndIsActiveTrueAndIsDeletedFalse(String cardNumber);

    Optional<Student> findByIdAndIsDeletedFalse(Long userId);
}
