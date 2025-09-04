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
import java.util.Set;


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
            AND (
                  :status = 'ALL'
                  OR (:status = 'ACTIVE' AND bc.epc IS NOT NULL)
                  OR (:status = 'INACTIVE' AND bc.epc IS NULL)
            )
            """)
    Page<BookCopyShortDTO> findByIsDeletedFalse(Pageable pageable,String status);

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
            AND (
                  :status = 'ALL'
                  OR (:status = 'ACTIVE' AND bc.epc IS NOT NULL)
                  OR (:status = 'INACTIVE' AND bc.epc IS NULL)
            )
            """)
    Page<BookCopyShortDTO> findByBookIdAndIsDeletedFalse(Integer baseBookId, Pageable pageable, String status);

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
            AND (
                  :status = 'ALL'
                  OR (:status = 'ACTIVE' AND bc.epc IS NOT NULL)
                  OR (:status = 'INACTIVE' AND bc.epc IS NULL)
            )
            """)
    Page<BookCopyShortDTO> findByEpcAndIsDeletedFalse(String epc, Pageable pageable, String status);

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
            AND (
                  :status = 'ALL'
                  OR (:status = 'ACTIVE' AND bc.epc IS NOT NULL)
                  OR (:status = 'INACTIVE' AND bc.epc IS NULL)
            )
            """)
    Page<BookCopyShortDTO> findByInventoryNumberAndIsDeletedFalse(String query, Pageable pageable, String status);

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
                    (:first is not null and lower(b.author) like :first)
                    or
                    (:second is not null and lower(b.title) like :second)
            ) and bc.isDeleted = false
            AND (
                  :status = 'ALL'
                  OR (:status = 'ACTIVE' AND bc.epc IS NOT NULL)
                  OR (:status = 'INACTIVE' AND bc.epc IS NULL)
            )
            """)
    Page<BookCopyShortDTO> findByTitleAndAuthor(String first, String second, Pageable pageable, String status);

    @Query("""
            select b.inventoryNumber from BookCopy  b where b.book.id = :bookId and b.isDeleted = false
            """)
    List<String> findInventoryNumberByBook_IdAndIsDeletedFalse(Integer bookId);

    boolean existsByEpcAndIsDeletedFalse(String epc);

    @Query("""
            select bc.inventoryNumber
            from BookCopy bc
            where bc.isDeleted = false
            and bc.inventoryNumber in :inventoryNumbers
            """)
    Set<String> existsInventoryNumbers(@Param("inventoryNumbers") List<String> inventoryNumbers);

}