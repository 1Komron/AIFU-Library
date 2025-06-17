package aifu.project.libraryweb.service.base_book_service;

import aifu.project.common_domain.dto.live_dto.BookCopyCreateDTO;
import aifu.project.common_domain.dto.live_dto.BookCopyResponseDTO;

import java.util.List;

public interface BookCopyService {
    BookCopyResponseDTO create(BookCopyCreateDTO dto);
    BookCopyResponseDTO update(Integer id, BookCopyCreateDTO dto);
    List<BookCopyResponseDTO> getAll();
    BookCopyResponseDTO getOne(Integer id);
    List<BookCopyResponseDTO> getAllByBaseBook(Integer baseBookId);
    void delete(Integer id);
    long count();
}