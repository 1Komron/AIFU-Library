package aifu.project.libraryweb.service.BaseBook;

import aifu.project.commondomain.dto.BookCopyDTO;
import aifu.project.commondomain.entity.BaseBook;
import aifu.project.commondomain.entity.BookCopy;
import aifu.project.commondomain.mapper.BookCopyMapper;
import aifu.project.commondomain.repository.BookCopyRepository;
import aifu.project.commondomain.repository.BaseBookRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BookCopyServiceImpl implements BookCopyService {

    private final BookCopyRepository bookCopyRepository;
    private final BaseBookRepository baseBookRepository;

    @Override
    public BookCopyDTO create(BookCopyDTO dto) {
        if (bookCopyRepository.existsByInventoryNumber(dto.getInventoryNumber())) {
            throw new RuntimeException("Inventory number already exists: " + dto.getInventoryNumber());
        }
        BaseBook baseBook = baseBookRepository.findById(dto.getBaseBookId())
                .orElseThrow(() -> new RuntimeException("BaseBook not found with id: " + dto.getBaseBookId()));
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
                .orElseThrow(() -> new RuntimeException("BaseBook not found with id: " + dto.getBaseBookId()));
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
                .orElseThrow(() -> new RuntimeException("BaseBook not found with id: " + baseBookId));
        return bookCopyRepository.findAllByBook(baseBook)
                .stream()
                .map(BookCopyMapper::toDto)
                .collect(Collectors.toList());
    }
}