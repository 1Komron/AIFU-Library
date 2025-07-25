package aifu.project.libraryweb.repository;

import aifu.project.common_domain.dto.BookCopyStats;
import aifu.project.common_domain.entity.BaseBook;
import aifu.project.common_domain.entity.BookCopy;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.Collection;


public interface BookCopyRepository extends JpaRepository<BookCopy, Integer> {

    Optional<BookCopy> findByInventoryNumber(String inventoryNumber);

    Collection<Object> findByBook(BaseBook baseBook);

    boolean existsByInventoryNumber(String inventoryNumber);

    boolean existsByInventoryNumberAndIsTakenTrue(String inventoryNumber);

    List<BookCopy> findAllByBook(BaseBook book);
    
    Page<BookCopy> findByIsDeletedFalse(Pageable pageable);

    Optional<BookCopy> findByIdAndIsDeletedFalse(Integer id);

    Page<BookCopy> findByBookIdAndIsDeletedFalse(Integer baseBookId, Pageable pageable);

    List<BookCopy> findByBook_IdAndIsDeletedFalse(Integer bookId);

    long countByBook_IdAndIsDeletedFalse(Integer bookId);

    long countByBook_IdAndIsTakenTrueAndIsDeletedFalse(Integer bookId);


    @Query("""
        SELECT new aifu.project.common_domain.dto.BookCopyStats(
            COUNT(bc.id),
            SUM(CASE WHEN bc.isTaken = true THEN 1 ELSE 0 END),
            bc.book.id
        )
        FROM BookCopy bc
        WHERE bc.isDeleted = false AND bc.book.id IN :bookIds
        GROUP BY bc.book.id
    """)
    List<BookCopyStats> getStatsForBooks(@Param("bookIds") List<Integer> bookIds);

    Optional<BookCopy> findByEpcAndIsDeletedFalse(String epc);
}