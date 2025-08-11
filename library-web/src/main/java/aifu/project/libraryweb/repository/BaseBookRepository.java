package aifu.project.libraryweb.repository;

import aifu.project.common_domain.entity.BaseBook;

import aifu.project.common_domain.entity.BaseBookCategory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;


@Repository
public interface BaseBookRepository extends JpaRepository<BaseBook, Integer> {

    boolean existsByIsbn(String isbn);

    Page<BaseBook> findByIsDeletedFalse(Pageable pageable);

    Optional<BaseBook> findByIdAndIsDeletedFalse(Integer id);

    List<BaseBook> findByCategory_IdAndIsDeletedFalse(Integer categoryId);

    @Query("""
            SELECT b FROM BaseBook b
            WHERE b.isDeleted = false AND (
                LOWER(b.author) LIKE LOWER(CONCAT('%', :query, '%')) OR
                LOWER(b.title) LIKE LOWER(CONCAT('%', :query, '%')) OR
                LOWER(b.series) LIKE LOWER(CONCAT('%', :query, '%')) OR
                LOWER(b.isbn) LIKE LOWER(CONCAT('%', :query, '%')) OR
                LOWER(b.udc) LIKE LOWER(CONCAT('%', :query, '%'))
            )
            """)
    Page<BaseBook> searchBooks(@Param("query") String query, Pageable pageable);

    @Query("select b from  BaseBook b where b.isDeleted = false and b.id = :id")
    Page<BaseBook> searchById(Long id, Pageable pageable);

    @Query("select b from  BaseBook b where b.isDeleted = false and lower(b.title) like lower(concat('%', :query, '%'))")
    Page<BaseBook> searchByTitle(String query, Pageable pageable);

    @Query("select b from  BaseBook b where b.isDeleted = false and lower(b.author) like lower(concat('%', :query, '%'))")
    Page<BaseBook> searchByAuthor(String query, Pageable pageable);

    @Query("select b from  BaseBook b where b.isDeleted = false and lower(b.isbn) like lower(concat('%', :query, '%'))")
    Page<BaseBook> searchByIsbn(String query, Pageable pageable);

    @Query("select b from  BaseBook b where b.isDeleted = false and lower(b.udc) like lower(concat('%', :query, '%'))")
    Page<BaseBook> searchByUdc(String query, Pageable pageable);

    @Query("select b from  BaseBook b where b.isDeleted = false and lower(b.series) like lower(concat('%', :query, '%'))")
    Page<BaseBook> searchSeries(String query, Pageable pageable);

    @Query("select b from  BaseBook b where b.isDeleted = false and b.category.id = :id")
    Page<BaseBook> searchByCategory_Id(int id, Pageable pageable);
}

