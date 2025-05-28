package aifu.project.librarybot.repository;

import aifu.project.commondomain.entity.BaseBook;
import aifu.project.commondomain.entity.BookCopy;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.Collection;
import java.util.Optional;


public interface BookCopyRepository extends JpaRepository<BookCopy, Integer> {
    Optional<BookCopy> findByInventoryNumber(String inventoryNumber);

    long countByBook(BaseBook book);

    Collection<Object> findByBook(BaseBook baseBook);


    boolean existsByInventoryNumber(String inventoryNumber);

    boolean existsByInventoryNumberAndIsTakenTrue(String inventoryNumber);

    List<BookCopy> findAllByBook(BaseBook book);


}