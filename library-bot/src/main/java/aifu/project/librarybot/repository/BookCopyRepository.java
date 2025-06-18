package aifu.project.librarybot.repository;

import aifu.project.common_domain.entity.BaseBook;
import aifu.project.common_domain.entity.BookCopy;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.Collection;


public interface BookCopyRepository extends JpaRepository<BookCopy, Integer> {
    Optional<BookCopy> findByInventoryNumberAndIsDeletedFalse(String inventoryNumber);

    long countByBook(BaseBook book);

    Collection<Object> findByBook(BaseBook baseBook);


    boolean existsByInventoryNumber(String inventoryNumber);

    boolean existsByInventoryNumberAndIsTakenTrue(String inventoryNumber);

    List<BookCopy> findAllByBook(BaseBook book);
}