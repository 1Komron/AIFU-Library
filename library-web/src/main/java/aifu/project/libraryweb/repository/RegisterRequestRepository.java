package aifu.project.libraryweb.repository;

import aifu.project.commondomain.entity.RegisterRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface RegisterRequestRepository extends JpaRepository<RegisterRequest, Long> {

    RegisterRequest findByUser_ChatId(long l);
}
