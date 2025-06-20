package aifu.project.libraryweb.repository;

import aifu.project.common_domain.entity.BaseBook;
import aifu.project.common_domain.entity.BookCopy;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.Collection;


public interface BookCopyRepository extends JpaRepository<BookCopy, Integer> {

    Optional<BookCopy> findByInventoryNumber(String inventoryNumber);

    Collection<Object> findByBook(BaseBook baseBook);

    boolean existsByInventoryNumber(String inventoryNumber);

    boolean existsByInventoryNumberAndIsTakenTrue(String inventoryNumber);

    List<BookCopy> findAllByBook(BaseBook book);
    
    Page<BookCopy> findByIsDeletedFalse(Pageable pageable);

    Optional<BookCopy> findByIdAndIsDeletedFalse(Integer id);

    Page<BookCopy> findByBookIdAndIsDeletedFalse(Integer baseBookId, Pageable pageable);

    List<BookCopy> findByBook_IdAndIsDeletedFalse(Integer bookId);
}