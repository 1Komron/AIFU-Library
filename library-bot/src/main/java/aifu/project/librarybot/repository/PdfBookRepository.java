package aifu.project.librarybot.repository;

import aifu.project.common_domain.entity.PdfBook;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PdfBookRepository extends JpaRepository<PdfBook, Integer> {
    List<PdfBook> findAll();
}