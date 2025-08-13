package aifu.project.libraryweb.repository;

import aifu.project.common_domain.dto.live_dto.BaseBookCategoryShortDTO;
import aifu.project.common_domain.entity.BaseBookCategory;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BaseBookCategoryRepository extends JpaRepository<BaseBookCategory, Integer> {
    @Query("""
            SELECT new aifu.project.common_domain.dto.live_dto.BaseBookCategoryShortDTO(c.id, c.name, COUNT(b))
                        FROM BaseBookCategory c
                            LEFT JOIN BaseBook b ON b.category = c AND b.isDeleted = false
                            WHERE c.isDeleted = false
                            GROUP BY c.id, c.name
            """)
    List<BaseBookCategoryShortDTO> findAllByIsDeletedFalse(Sort sort);

    Optional<BaseBookCategory> findByIdAndIsDeletedFalse(Integer id);

    @Query("SELECT COUNT(c) > 0 FROM BaseBookCategory c WHERE LOWER(c.name) = LOWER(:name) and c.isDeleted = false")
    boolean existsByNameAndIsDeletedFalse(String name);

}
