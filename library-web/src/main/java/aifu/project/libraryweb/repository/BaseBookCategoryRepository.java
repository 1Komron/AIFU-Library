package aifu.project.libraryweb.repository;

import aifu.project.common_domain.dto.BaseBookCategoryDTO;
import aifu.project.common_domain.entity.BaseBookCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BaseBookCategoryRepository extends JpaRepository<BaseBookCategory, Integer> {
    List<BaseBookCategoryDTO> findAllByIsDeletedFalse();

    Optional<BaseBookCategory> findByIdAndIsDeletedFalse(Integer id);

    boolean existsByIdAndIsDeletedFalse(Integer categoryId);
}
