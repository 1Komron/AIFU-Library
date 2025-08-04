package aifu.project.libraryweb.repository;

import aifu.project.common_domain.entity.History;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface HistoryRepository extends JpaRepository<History, Long> {
    @Query("SELECT h FROM History h WHERE h.user.id = :id and h.user.isDeleted = false")
    Page<History> findByUserId(Long id, Pageable pageRequest);

    @Query("SELECT h FROM History h WHERE h.user.cardNumber = :query and h.user.isDeleted = false")
    Page<History> findByUserCardNumber(String query, Pageable pageRequest);

    @Query("SELECT h FROM History h WHERE h.book.inventoryNumber = :query and h.book.isDeleted = false")
    Page<History> findByBookInventoryNumber(String query, Pageable pageRequest);
}
