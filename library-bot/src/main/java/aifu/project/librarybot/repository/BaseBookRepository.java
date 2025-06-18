package aifu.project.librarybot.repository;

import aifu.project.common_domain.entity.BaseBook;
import aifu.project.common_domain.entity.BaseBookCategory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BaseBookRepository extends JpaRepository<BaseBook, Integer> {

    boolean existsByIsbn(String isbn);

    @EntityGraph(attributePaths = {"copies", "category"}, type = EntityGraph.EntityGraphType.LOAD)
    List<BaseBook> findByIdIn(List<Integer> ids);

    Page<BaseBook> findByCategoryAndIsDeletedFalse(BaseBookCategory category, Pageable pageable);

    @EntityGraph(attributePaths = {"copies"}, type = EntityGraph.EntityGraphType.LOAD)
    Optional<BaseBook> findBookByIdAndIsDeletedFalse(Integer id);
}

