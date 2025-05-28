package aifu.project.libraryweb.service.base_book;

import aifu.project.commondomain.dto.BookCopyDTO;
import java.util.List;

public interface BookCopyService {
    BookCopyDTO create(BookCopyDTO dto);
    BookCopyDTO update(Integer id, BookCopyDTO dto);
    void delete(Integer id);
    BookCopyDTO getById(Integer id);
    List<BookCopyDTO> getAllByBaseBook(Integer baseBookId);
}