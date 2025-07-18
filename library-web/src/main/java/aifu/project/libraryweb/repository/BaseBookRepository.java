package aifu.project.libraryweb.repository;

import aifu.project.common_domain.dto.BookCopyStats;
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

    boolean existsByIdAndIsDeletedFalse(Integer baseBookId);

    List<BaseBook> findByCategory_IdAndIsDeletedFalse(Integer categoryId);

    Page<BaseBook> findAllByCategoryAndIsDeletedFalse(BaseBookCategory category, Pageable pageable);
}

