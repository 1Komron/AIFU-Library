package aifu.project.libraryweb.repository;

import aifu.project.common_domain.entity.Student;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Repository
public interface StudentRepository extends JpaRepository<Student, Long> {

    @Query("select count(*) from Student s where s.isDeleted = false")
    long getStudentsCount();

    @Query("""
                        select s from Student s
                        where s.id = :id
                        and s.isDeleted = false
                        and s.isActive  in :statusList
            """)
    Page<Student> findByIdAndIsDeletedFalse(Long id, Pageable pageable, List<Boolean> statusList);

    @Query("""
                select s from Student s
                where s.cardNumber = :cardNumber
                  and s.isDeleted = false
                  and s.isActive in :statusList
            """)
    Page<Student> findByCardNumberAndIsDeletedFalse(String cardNumber, Pageable pageable, List<Boolean> statusList);

    @Query("""
                select s from Student s
                where s.isDeleted = false
                  and s.isActive in :statusList
            """)
    Page<Student> findByIsDeletedFalse(Pageable pageable, List<Boolean> statusList);

    @Query("""
                select s from Student s
                where
                  (
                    (lower(s.surname) like :first or lower(s.name) like :first)
                    or
                    (:second is not null and
                      (
                        (lower(s.surname) like :first and lower(s.name) like :second)
                        or
                        (lower(s.surname) like :second and lower(s.name) like :first)
                      )
                    )
                  )
                  and s.isDeleted = false
                  and s.isActive in :statusList
            """)
    Page<Student> findBySurnameAndName(String first, String second, Pageable pageable, List<Boolean> statusList);

    Optional<Student> findByCardNumberAndIsDeletedFalse(String cardNumber);

    Optional<Student> findByIdAndIsDeletedFalse(Long userId);

    @Query("SELECT s.passportCode FROM Student s WHERE s.passportCode IN :hashedPassportCodes")
    Set<String> findExistingHashedPassportCodes(@Param("hashedPassportCodes") Set<String> hashedPassportCodes);

    List<Student> findByPassportCodeInAndIsDeletedFalse(Set<String> strings);

}

