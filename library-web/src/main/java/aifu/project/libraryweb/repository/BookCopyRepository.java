package aifu.project.libraryweb.repository;

import aifu.project.common_domain.dto.BookCopyStats;
import aifu.project.common_domain.dto.live_dto.BookCopyShortDTO;
import aifu.project.common_domain.entity.BookCopy;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;


public interface BookCopyRepository extends JpaRepository<BookCopy, Integer> {

    @Query("""
            SELECT new aifu.project.common_domain.dto.live_dto.BookCopyShortDTO(
                bc.id,
                b.author,
                b.title,
                bc.inventoryNumber,
                bc.shelfLocation,
                bc.isTaken
            )
            FROM BookCopy bc
            JOIN bc.book b
            WHERE bc.isDeleted = false
            """)
    Page<BookCopyShortDTO> findByIsDeletedFalse(Pageable pageable);

    Optional<BookCopy> findByIdAndIsDeletedFalse(Integer id);

    @Query("""
            SELECT new aifu.project.common_domain.dto.live_dto.BookCopyShortDTO(
                bc.id,
                b.author,
                b.title,
                bc.inventoryNumber,
                bc.shelfLocation,
                bc.isTaken
            )
            FROM BookCopy bc
            JOIN bc.book b
            WHERE b.id = :baseBookId AND bc.isDeleted = false
            """)
    Page<BookCopyShortDTO> findByBookIdAndIsDeletedFalse(Integer baseBookId, Pageable pageable);

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

    @Query("""
            SELECT new aifu.project.common_domain.dto.live_dto.BookCopyShortDTO(
                bc.id,
                b.author,
                b.title,
                bc.inventoryNumber,
                bc.shelfLocation,
                bc.isTaken
            )
            from BookCopy bc
            JOIN bc.book b
            where bc.isDeleted = false
            and bc.epc = :epc
            """)
    Page<BookCopyShortDTO> findByEpcAndIsDeletedFalse(String epc, Pageable pageable);

    @Query("""
            SELECT       new aifu.project.common_domain.dto.live_dto.BookCopyShortDTO(
                bc.id,
                b.author,
                b.title,
                bc.inventoryNumber,
                bc.shelfLocation,
                bc.isTaken
            )
            from BookCopy bc
            JOIN bc.book b
            where bc.isDeleted = false
            and bc.inventoryNumber ilike concat('%', :query, '%')
            """)
    Page<BookCopyShortDTO> findByInventoryNumberAndIsDeletedFalse(String query, Pageable pageable);

    Optional<BookCopy> findByInventoryNumberAndIsDeletedFalse(String inventoryNumber);

    boolean existsByInventoryNumberAndIsDeletedFalse(String inventoryNumber);

    @Query("""
            select  new aifu.project.common_domain.dto.live_dto.BookCopyShortDTO(
                bc.id,
                b.author,
                b.title,
                bc.inventoryNumber,
                bc.shelfLocation,
                bc.isTaken
            )from BookCopy bc
            join bc.book b
            where (
                    (lower(b.title) like : first or lower(b.author) like : first)
                    or
                    (:second is not null and
                      (
                        (lower(b.author) like :first and lower(b.title) like :second)
                        or
                        (lower(b.author) like :second and lower(b.title) like :first)
                      )
                    )
            ) and bc.isDeleted = false
            """)
    Page<BookCopyShortDTO> findByTitleAndAuthor(String first, String second, Pageable pageable);

    @Query("""
            select b.inventoryNumber from BookCopy  b where b.book.id = :bookId and b.isDeleted = false
            """)
    List<String> findInventoryNumberByBook_IdAndIsDeletedFalse(Integer bookId);
}