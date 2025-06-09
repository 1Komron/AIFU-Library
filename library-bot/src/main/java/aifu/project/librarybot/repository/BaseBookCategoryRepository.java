package aifu.project.librarybot.repository;

import aifu.project.common_domain.entity.BaseBookCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BaseBookCategoryRepository extends JpaRepository<BaseBookCategory, Integer> {
}
