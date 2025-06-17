package aifu.project.libraryweb.service.base_book_service;

import aifu.project.common_domain.dto.live_dto.BookCopyCreateDTO;
import aifu.project.common_domain.dto.live_dto.BookCopyResponseDTO;
import aifu.project.common_domain.entity.BaseBook;
import aifu.project.common_domain.entity.BookCopy;
import aifu.project.common_domain.mapper.BookCopyMapper;
import aifu.project.libraryweb.repository.BaseBookRepository;
import aifu.project.libraryweb.repository.BookCopyRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class BookCopyServiceImpl implements BookCopyService {

    private final BookCopyRepository bookCopyRepository;
    private final BaseBookRepository baseBookRepository;

    public BookCopyServiceImpl(BookCopyRepository bookCopyRepository, BaseBookRepository baseBookRepository) {
        this.bookCopyRepository = bookCopyRepository;
        this.baseBookRepository = baseBookRepository;
    }

    @Override
    public BookCopyResponseDTO create(BookCopyCreateDTO dto) {
        BaseBook baseBook = baseBookRepository.findById(dto.getBaseBookId())
                .orElseThrow(() -> new RuntimeException("BaseBook not found"));

        BookCopy entity = BookCopyMapper.toEntity(dto, baseBook);
        bookCopyRepository.save(entity);
        return BookCopyMapper.toResponseDTO(entity);
    }

    @Override
    public BookCopyResponseDTO update(Integer id, BookCopyCreateDTO dto) {
        return null;
    }

    @Override
    public List<BookCopyResponseDTO> getAll() {
        return bookCopyRepository.findAll().stream()
                .map(BookCopyMapper::toResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    public BookCopyResponseDTO getOne(Integer id) {
        BookCopy entity = bookCopyRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("BookCopy not found"));
        return BookCopyMapper.toResponseDTO(entity);
    }

    @Override
    public List<BookCopyResponseDTO> getAllByBaseBook(Integer baseBookId) {
        return List.of();
    }

    @Override
    public void delete(Integer id) {
        bookCopyRepository.deleteById(id);
    }
    @Override
    public long count() {
        return bookCopyRepository.count();
    }
}
