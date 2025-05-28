package aifu.project.librarybot.repository;

import aifu.project.commondomain.entity.BaseBook;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BaseBookRepository extends JpaRepository<BaseBook, Integer> {

    boolean existsByIsbn(String isbn);
}

