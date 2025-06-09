package aifu.project.libraryweb.service.base_book_service;

import aifu.project.common_domain.dto.live_dto.BookCopyDTO;
import aifu.project.common_domain.entity.BaseBook;
import aifu.project.common_domain.entity.BookCopy;
import aifu.project.common_domain.mapper.BookCopyMapper;
import aifu.project.libraryweb.repository.BaseBookRepository;
import aifu.project.libraryweb.repository.BookCopyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BookCopyServiceImpl implements BookCopyService {

    private final BookCopyRepository bookCopyRepository;
    private final BaseBookRepository baseBookRepository;

    private static final String BASE_BOOK_NOT_FOUND = "BaseBook not found with id: ";

    @Override
    public BookCopyDTO create(BookCopyDTO dto) {
        if (bookCopyRepository.existsByInventoryNumber(dto.getInventoryNumber())) {
            throw new RuntimeException("Inventory number already exists: " + dto.getInventoryNumber());
        }
        BaseBook baseBook = baseBookRepository.findById(dto.getBaseBookId())
                .orElseThrow(() -> new RuntimeException(BASE_BOOK_NOT_FOUND + dto.getBaseBookId()));
        BookCopy copy = BookCopyMapper.toEntity(dto, baseBook);
        BookCopy saved = bookCopyRepository.save(copy);
        return BookCopyMapper.toDto(saved);
    }

    @Override
    public BookCopyDTO update(Integer id, BookCopyDTO dto) {
        BookCopy existing = bookCopyRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("BookCopy not found with id: " + id));
        if (!existing.getInventoryNumber().equals(dto.getInventoryNumber()) &&
                bookCopyRepository.existsByInventoryNumber(dto.getInventoryNumber())) {
            throw new RuntimeException("Inventory number already exists: " + dto.getInventoryNumber());
        }
        BaseBook baseBook = baseBookRepository.findById(dto.getBaseBookId())
                .orElseThrow(() -> new RuntimeException(BASE_BOOK_NOT_FOUND + dto.getBaseBookId()));
        existing.setInventoryNumber(dto.getInventoryNumber());
        existing.setShelfLocation(dto.getShelfLocation());
        existing.setNotes(dto.getNotes());
        existing.setBook(baseBook);
        BookCopy updated = bookCopyRepository.save(existing);
        return BookCopyMapper.toDto(updated);
    }

    @Override
    public void delete(Integer id) {
        if (!bookCopyRepository.existsById(id)) {
            throw new RuntimeException("BookCopy not found with id: " + id);
        }
        bookCopyRepository.deleteById(id);
    }

    @Override
    public BookCopyDTO getById(Integer id) {
        BookCopy copy = bookCopyRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("BookCopy not found with id: " + id));
        return BookCopyMapper.toDto(copy);
    }

    @Override
    public List<BookCopyDTO> getAllByBaseBook(Integer baseBookId) {
        BaseBook baseBook = baseBookRepository.findById(baseBookId)
                .orElseThrow(() -> new RuntimeException(BASE_BOOK_NOT_FOUND + baseBookId));
        return bookCopyRepository.findAllByBook(baseBook)
                .stream()
                .map(BookCopyMapper::toDto)
                .toList();
    }
}