package aifu.project.commondomain.repository;

import aifu.project.commondomain.entity.BaseBook;
import aifu.project.commondomain.entity.BookCopy;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.Collection;


public interface BookCopyRepository extends JpaRepository<BookCopy, Long> {
    Optional<BookCopy> findByInventoryNumber(String inventoryNumber);
    long countByBook(BaseBook book);

    Collection<Object> findByBook(BaseBook baseBook);


    boolean existsByInventoryNumber(String inventoryNumber);

    boolean existsByInventoryNumberAndIsTakenTrue(String inventoryNumber);

}