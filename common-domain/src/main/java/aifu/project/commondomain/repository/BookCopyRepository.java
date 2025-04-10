package aifu.project.commondomain.repository;

import aifu.project.commondomain.entity.BaseBook;
import aifu.project.commondomain.entity.BookCopy;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;

@Repository
public interface BookCopyRepository extends JpaRepository<BookCopy, Long> {

    Collection<Object> findByBook(BaseBook baseBook);
}
