package aifu.project.libraryweb.repository;

import aifu.project.common_domain.dto.pdf_book_dto.CategoryShortDTO;
import aifu.project.common_domain.entity.Category;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface CategoryRepository extends JpaRepository<Category, Integer> {

    Page<Category> findByNameContainingIgnoreCase(String name, Pageable pageable);

    @Query("""
                SELECT new aifu.project.common_domain.dto.pdf_book_dto.CategoryShortDTO(
                    c.id,
                    c.name,
                    COUNT(b)
                )
                FROM Category c
                LEFT JOIN c.books b
                GROUP BY c.id, c.name
            """)
    List<CategoryShortDTO> findAllCategories(Sort sort);

}