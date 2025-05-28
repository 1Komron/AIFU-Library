package aifu.project.commondomain.repository;

import aifu.project.commondomain.entity.BaseBook;
import aj.org.objectweb.asm.commons.Remapper;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;

@Repository
public interface BaseBookRepository extends JpaRepository<BaseBook, Integer> {

    boolean existsByIsbn(String isbn);
}

