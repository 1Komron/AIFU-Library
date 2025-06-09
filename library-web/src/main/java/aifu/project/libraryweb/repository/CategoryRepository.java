package aifu.project.libraryweb.repository;

import aifu.project.common_domain.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoryRepository extends JpaRepository<Category, Integer> {

}