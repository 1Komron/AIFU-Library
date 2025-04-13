package aifu.project.commondomain.repository;

import aifu.project.commondomain.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    List<User> findByChatId(Long chatId);

    boolean existsUserByChatId(Long chatId);

    boolean existsByChatIdAndIsActive(Long chatId, boolean isActive);

}
