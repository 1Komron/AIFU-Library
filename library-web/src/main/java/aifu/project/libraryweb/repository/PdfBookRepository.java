package aifu.project.libraryweb.repository;

import aifu.project.common_domain.entity.Category;
import aifu.project.common_domain.entity.PdfBook;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;

public interface PdfBookRepository extends JpaRepository <PdfBook, Integer> {

    Page<PdfBook> findByCategoryId(int i, Pageable pageable);

    @Query("""
            SELECT b from PdfBook b
            where
                  (
                    (lower(b.author) like :first or lower(b.title) like :first)
                    or
                    (:second is not null and
                      (
                        (lower(b.author) like :first and lower(b.title) like :second)
                        or
                        (lower(b.author) like :second and lower(b.title) like :first)
                      )
                    )
                  )
            """)
    Page<PdfBook> findByAuthorAndTitle(String first, String second, Pageable pageable);

    Page<PdfBook> findAllByCategory_Id(int category, Pageable pageable);
}
