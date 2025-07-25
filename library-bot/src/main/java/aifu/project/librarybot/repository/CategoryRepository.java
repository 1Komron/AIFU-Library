package aifu.project.librarybot.repository;

import aifu.project.common_domain.entity.Category;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoryRepository extends JpaRepository<Category, Integer> {

    Page<Category> findByNameContainingIgnoreCase(String name, Pageable pageable);

}