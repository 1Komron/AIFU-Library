package aifu.project.libraryweb.repository;

import aifu.project.common_domain.entity.Librarian;
import aifu.project.common_domain.entity.enums.Role;
import org.apache.poi.sl.draw.geom.GuideIf;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;


public interface LibrarianRepository extends JpaRepository<Librarian, Long> {

    Optional<Librarian> findByEmailAndIsDeletedFalse(String username);

    Optional<Librarian> findByEmail(String email);

    boolean existsByEmail(String email);

    boolean existsByRole(Role role);

    Optional<Librarian> findByEmailAndIsActiveFalse(String email);

}

