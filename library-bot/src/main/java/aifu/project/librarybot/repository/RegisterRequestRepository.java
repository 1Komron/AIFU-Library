package aifu.project.librarybot.repository;

import aifu.project.common_domain.entity.RegisterRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface RegisterRequestRepository extends JpaRepository<RegisterRequest, Long> {

    RegisterRequest findByUser_ChatId(long l);
}
