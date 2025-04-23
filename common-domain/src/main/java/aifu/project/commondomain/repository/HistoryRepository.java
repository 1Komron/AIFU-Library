package aifu.project.commondomain.repository;

import aifu.project.commondomain.entity.History;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HistoryRepository extends JpaRepository<History, Long> {
    @Query("""
                select h from History h
                join fetch h.book bc
                join fetch bc.book
                where h.user.chatId = :chatId
            """)
    List<History> findAllByUser_ChatId(Long chatId);
}
