package aifu.project.libraryweb.repository;

import aifu.project.common_domain.entity.Librarian;
import aifu.project.common_domain.entity.enums.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;


import java.util.Optional;

@EnableJpaRepositories
public interface LibrarianRepository extends JpaRepository<Librarian, Long> {

    Optional<Librarian> findByEmailAndIsDeletedFalse(String username);

    Optional<Librarian> findByEmail(String email);

    boolean existsByEmail(String email);

    boolean existsByRole(Role role);

    Optional<Librarian> findByEmailAndIsActiveFalse(String email);

    @Query("SELECT l FROM Librarian l WHERE l.id = :id AND l.role = 'ADMIN' AND l.isDeleted = false")
    Optional<Librarian> findByID(Long id);

}

