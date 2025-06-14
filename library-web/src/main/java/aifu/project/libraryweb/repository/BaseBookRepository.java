package aifu.project.libraryweb.repository;

import aifu.project.common_domain.entity.BaseBook;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface BaseBookRepository extends JpaRepository<BaseBook, Integer> {

    boolean existsByIsbn(String isbn);
}

