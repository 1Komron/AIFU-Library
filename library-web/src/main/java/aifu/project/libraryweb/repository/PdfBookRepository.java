package aifu.project.libraryweb.repository;

import aifu.project.common_domain.entity.PdfBook;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
public interface PdfBookRepository extends JpaRepository <PdfBook, Integer> {

    List<PdfBook> findByCategoryId(Integer categoryId);

    Page<PdfBook> findByTitleContainingIgnoreCase(String title, Pageable pageable);

    Page<PdfBook> findByAuthorContainingIgnoreCase(String author, Pageable pageable);
}
