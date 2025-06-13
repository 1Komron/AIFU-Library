package aifu.project.librarybot.repository;

import aifu.project.common_domain.entity.History;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;

@Repository
public interface HistoryRepository extends JpaRepository<History, Long> {
    @Query(
            value = """
            SELECT DISTINCT h
              FROM History h
              JOIN FETCH h.book bc
              JOIN FETCH bc.book
             WHERE h.user.chatId = :chatId
                     order by h.returnedAt
        """,
            countQuery = """
            SELECT COUNT(h)
              FROM History h
             WHERE h.user.chatId = :chatId
        """
    )
    Page<History> findAllByUserChatId(
            @Param("chatId") Long chatId,
            Pageable pageable
    );

    long countByGivenAtBetween(LocalDate givenAtAfter, LocalDate givenAtBefore);
}
