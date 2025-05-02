package aifu.project.commondomain.repository;

import aifu.project.commondomain.entity.PdfBook;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PdfBookRepository extends JpaRepository<PdfBook, Integer> {
}