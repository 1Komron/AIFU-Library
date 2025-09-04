package aifu.project.libraryweb.repository;

import aifu.project.common_domain.dto.excel_dto.BookExcelDTO;
import aifu.project.common_domain.entity.BaseBook;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;


@Repository
public interface BaseBookRepository extends JpaRepository<BaseBook, Integer> {

    Page<BaseBook> findByIsDeletedFalse(Pageable pageable);

    Optional<BaseBook> findByIdAndIsDeletedFalse(Integer id);

    List<BaseBook> findByCategory_IdAndIsDeletedFalse(Integer categoryId);

    @Query("select b from  BaseBook b where b.isDeleted = false and b.id = :id")
    Page<BaseBook> searchById(Long id, Pageable pageable);

    @Query("select b from  BaseBook b where b.isDeleted = false and lower(b.isbn) like lower(concat('%', :query, '%'))")
    Page<BaseBook> searchByIsbn(String query, Pageable pageable);

    @Query("select b from  BaseBook b where b.isDeleted = false and lower(b.udc) like lower(concat('%', :query, '%'))")
    Page<BaseBook> searchByUdc(String query, Pageable pageable);

    @Query("select b from  BaseBook b where b.isDeleted = false and lower(b.series) like lower(concat('%', :query, '%'))")
    Page<BaseBook> searchSeries(String query, Pageable pageable);

    @Query("select b from  BaseBook b where b.isDeleted = false and b.category.id = :id")
    Page<BaseBook> searchByCategory_Id(int id, Pageable pageable);

    @Query("""
            select b from BaseBook b
            where (
                   (:first is not null and lower(b.author) like :first)
                    or
                   (:second is not null and lower(b.title) like :second)
            ) and b.isDeleted = false
            """)
    Page<BaseBook> searchByTitleAndAuthor(String first, String second, Pageable pageable);

    @Query("""
            select new aifu.project.common_domain.dto.excel_dto.BookExcelDTO(
                           bb.id,
                           bb.author,
                           bb.title,
                           bb.category.name,
                           bb.series,
                           bb.publicationYear,
                           bb.publisher,
                           bb.publicationCity,
                           bb.isbn,
                           bb.pageCount,
                           bb.language,
                           bb.udc,
                           null,
                           null
                        )
            from BaseBook bb
            left join bb.copies c
            """)
    List<BookExcelDTO> getAllBooks();
}

