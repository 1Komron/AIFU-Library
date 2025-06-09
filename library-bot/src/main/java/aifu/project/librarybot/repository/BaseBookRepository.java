package aifu.project.librarybot.repository;

import aifu.project.commondomain.entity.BaseBook;
import aifu.project.commondomain.entity.BaseBookCategory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BaseBookRepository extends JpaRepository<BaseBook, Integer> {

    boolean existsByIsbn(String isbn);

    @EntityGraph(attributePaths = {"copies", "category"}, type = EntityGraph.EntityGraphType.LOAD)
    List<BaseBook> findByIdIn(List<Integer> ids);

    Page<BaseBook> findByCategory(BaseBookCategory category, Pageable pageable);
}

