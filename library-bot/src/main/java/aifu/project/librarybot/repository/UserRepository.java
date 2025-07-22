package aifu.project.librarybot.repository;

import aifu.project.common_domain.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.beans.Transient;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    boolean existsUserByChatIdAndIsActiveTrueAndIsDeletedFalse(Long chatId);

    Optional<User> findByChatIdAndIsDeletedFalse(Long chatId);

    Optional<User> findByIdAndIsDeletedFalse(Long userId);

    Optional<User> findByChatId(Long chatId);

    User findByIsDeletedFalseAndPassportCode(String passportCode);
}
