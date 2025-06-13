package aifu.project.libraryweb.repository;

import aifu.project.common_domain.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.beans.Transient;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    boolean existsUserByChatId(Long chatId);

    boolean existsByChatIdAndIsActive(Long chatId, boolean isActive);

    User findByChatId(Long chatId);

    @Transient
    @Modifying
    void deleteByChatId(Long chatId);

    @Query("select count(*) from User u where u.role = 'USER'")
    long getUsersCount();
}
