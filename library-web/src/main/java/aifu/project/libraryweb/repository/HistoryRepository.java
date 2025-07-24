package aifu.project.libraryweb.repository;

import aifu.project.common_domain.entity.History;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;

@Repository
public interface HistoryRepository extends JpaRepository<History, Long> {
    long countByGivenAtBetween(LocalDate from, LocalDate localDate);
}
