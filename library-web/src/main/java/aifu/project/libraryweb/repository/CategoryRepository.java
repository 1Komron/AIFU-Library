package aifu.project.libraryweb.repository;

import aifu.project.common_domain.dto.pdf_book_dto.CategoryShortDTO;
import aifu.project.common_domain.entity.Category;
import jakarta.validation.constraints.NotBlank;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

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

    Category findByName(String name);

    boolean existsByName(String name);

    @Query(value = """
            select *
            from category c
            where (select count(p.id) from pdf_book p where p.category_id = c.id) >= 6
            order by random()
            limit :limit
            """, nativeQuery = true)
    List<Category> findRandomCategoriesWithBooks(@Param("limit") int limit);

}