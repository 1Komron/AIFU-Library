package aifu.project.libraryweb.repository;

import aifu.project.common_domain.entity.BaseBook;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;


@Repository
public interface BaseBookRepository extends JpaRepository<BaseBook, Integer> {

    boolean existsByIsbn(String isbn);

    Page<BaseBook> findByIsDeletedFalse(Pageable pageable);

    Optional<BaseBook> findByIdAndIsDeletedFalse(Integer id);
}

