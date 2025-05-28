package aifu.project.commondomain.repository;

import aifu.project.commondomain.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.beans.Transient;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    boolean existsUserByChatId(Long chatId);

    boolean existsByChatIdAndIsActive(Long chatId, boolean isActive);

    Optional<User> findByChatId(Long chatId);

    @Query("select u from User u where u.role = 'LIBRARIAN'")
    Optional<User> findLibrarian();

    @Transient
    @Modifying
    void deleteByChatId(Long chatId);
}
