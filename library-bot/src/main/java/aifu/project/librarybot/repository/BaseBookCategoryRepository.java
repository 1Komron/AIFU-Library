package aifu.project.librarybot.repository;

import aifu.project.commondomain.entity.BaseBookCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BaseBookCategoryRepository extends JpaRepository<BaseBookCategory, Integer> {
}
