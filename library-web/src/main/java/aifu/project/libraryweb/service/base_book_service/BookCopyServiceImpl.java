package aifu.project.libraryweb.service.base_book_service;

import aifu.project.common_domain.dto.BookCopyStats;
import aifu.project.common_domain.dto.live_dto.BookCopyCreateDTO;
import aifu.project.common_domain.dto.live_dto.BookCopyResponseDTO;
import aifu.project.common_domain.entity.BaseBook;
import aifu.project.common_domain.entity.BookCopy;
import aifu.project.common_domain.exceptions.BaseBookNotFoundException;
import aifu.project.common_domain.exceptions.BookCopyIsTakenException;
import aifu.project.common_domain.exceptions.BookCopyNotFoundException;
import aifu.project.common_domain.mapper.BookCopyMapper;
import aifu.project.common_domain.dto.ResponseMessage;
import aifu.project.libraryweb.repository.BaseBookRepository;
import aifu.project.libraryweb.repository.BookCopyRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class BookCopyServiceImpl implements BookCopyService {
    private final BookCopyRepository bookCopyRepository;
    private final BaseBookRepository baseBookRepository;

    @Override
    public ResponseEntity<ResponseMessage> create(BookCopyCreateDTO dto) {
        BaseBook baseBook = baseBookRepository.findByIdAndIsDeletedFalse(dto.getBaseBookId())
                .orElseThrow(() -> new BaseBookNotFoundException(dto.getBaseBookId()));

        //epc qoshilishi kerak
        BookCopy entity = BookCopyMapper.toEntity(dto, baseBook);
        entity = bookCopyRepository.save(entity);

        log.info("BookCopy created: {}", entity);

        BookCopyResponseDTO responseDTO = BookCopyMapper.toResponseDTO(entity);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ResponseMessage(true, "BookCopy created successfully", responseDTO));
    }

    @Override
    public ResponseEntity<ResponseMessage> update(Integer id, Map<String, Object> updates) {
        BookCopy bookCopy = bookCopyRepository.findByIdAndIsDeletedFalse(id)
                .orElseThrow(() -> new BookCopyNotFoundException(BookCopyNotFoundException.BY_ID + id));

        updates.forEach((key, value) -> {

            if (value == null)
                throw new IllegalArgumentException("BookCopy update. Value is null");

            if (key == null) {
                throw new IllegalArgumentException("BookCopy update. Key is null");
            }

            switch (key) {
                case "inventoryNumber" -> bookCopy.setInventoryNumber((String) value);
                case "shelfLocation" -> bookCopy.setShelfLocation((String) value);
                case "notes" -> bookCopy.setNotes((String) value);
                case "book" -> {
                    Integer baseBookId = Integer.parseInt((String) value);
                    BaseBook baseBook = baseBookRepository.findByIdAndIsDeletedFalse(baseBookId)
                            .orElseThrow(() -> new BaseBookNotFoundException(baseBookId));

                    bookCopy.setBook(baseBook);
                }
                default -> throw new IllegalArgumentException("BookCopy update. Invalid field. Key is: " + key);
            }
        });

        log.info("BookCopy updated: {}.\nUpdated fields: {}", bookCopy, updates.keySet());

        bookCopyRepository.save(bookCopy);

        BookCopyResponseDTO responseDTO = BookCopyMapper.toResponseDTO(bookCopy);
        return ResponseEntity.ok(new ResponseMessage(true, "BookCopy updated successfully", responseDTO));
    }

    @Override
    public ResponseEntity<ResponseMessage> getAll(int pageNumber, int pageSize, String sortDirection) {
        Sort.Direction direction = sortDirection.equalsIgnoreCase("asc") ? Sort.Direction.ASC : Sort.Direction.DESC;
        Pageable pageable = PageRequest.of(--pageNumber, pageSize, Sort.by(direction, "id"));
        Page<BookCopy> page = bookCopyRepository.findByIsDeletedFalse(pageable);

        List<BookCopyResponseDTO> list = page.getContent().stream()
                .map(BookCopyMapper::toResponseDTO)
                .toList();

        Map<String, Object> map = Map.of(
                "list", list,
                "currentPage", page.getNumber() + 1,
                "totalPages", page.getTotalPages(),
                "totalElements", page.getTotalElements()
        );

        return ResponseEntity.ok(new ResponseMessage(true, "BookCopy list", map));
    }

    @Override
    public ResponseEntity<ResponseMessage> get(Integer id) {
        BookCopy entity = bookCopyRepository.findByIdAndIsDeletedFalse(id)
                .orElseThrow(() -> new BookCopyNotFoundException(BookCopyNotFoundException.BY_ID + id));

        BookCopyResponseDTO responseDTO = BookCopyMapper.toResponseDTO(entity);

        return ResponseEntity.ok(new ResponseMessage(true, "BookCopy", responseDTO));
    }

    @Override
    public ResponseEntity<ResponseMessage> search(String query, String field, int pageNumber, int pageSize, String sortDirection) {
        Sort.Direction direction = sortDirection.equalsIgnoreCase("asc") ? Sort.Direction.ASC : Sort.Direction.DESC;
        Pageable pageable = PageRequest.of(--pageNumber, pageSize, Sort.by(direction, "id"));

        Page<BookCopy> page = switch (field) {
            case "book" -> bookCopyRepository.findByBookIdAndIsDeletedFalse(Integer.parseInt(query), pageable);

            case "inventoryNumber" -> bookCopyRepository.findByInventoryNumberAndIsDeletedFalse(query, pageable);

            case "epc" -> bookCopyRepository.findByEpcAndIsDeletedFalse(query, pageable);

            default -> throw new IllegalArgumentException("BookCopy search. Invalid field: " + field);
        };

        List<BookCopyResponseDTO> list = page.getContent().stream()
                .map(BookCopyMapper::toResponseDTO)
                .toList();

        Map<String, Object> map = Map.of(
                "list", list,
                "currentPage", page.getNumber() + 1,
                "totalPages", page.getTotalPages(),
                "totalElements", page.getTotalElements()
        );

        return ResponseEntity.ok(new ResponseMessage(true, "BookCopy list", map));
    }

    @Override
    public ResponseEntity<ResponseMessage> delete(Integer id) {
        BookCopy bookCopy = bookCopyRepository.findByIdAndIsDeletedFalse(id)
                .orElseThrow(() -> new BookCopyNotFoundException(BookCopyNotFoundException.BY_ID + id));

        if (bookCopy.isTaken())
            throw new BookCopyIsTakenException("BookCopy has been taken. Id: " + bookCopy.getId());

        bookCopy.setDeleted(true);
        bookCopyRepository.save(bookCopy);

        log.info("BookCopy deleted by id: {}.", id);

        return ResponseEntity.ok(new ResponseMessage(true, "BookCopy deleted", id));
    }


    public Map<Integer, BookCopyStats> getStatsMap(List<Integer> bookIds) {
        return bookCopyRepository.getStatsForBooks(bookIds).stream()
                .collect(Collectors.toMap(BookCopyStats::baseBookId, stat -> stat));
    }

    @Override
    public Map<String, Long> getTotalAndTakenCount(Integer baseBookId) {
        long total = bookCopyRepository.countByBook_IdAndIsDeletedFalse(baseBookId);
        long count = bookCopyRepository.countByBook_IdAndIsTakenTrueAndIsDeletedFalse(baseBookId);
        return Map.of(
                "total", total,
                "taken", count
        );
    }

    @Override
    public BookCopy findByEpc(String epc) {
        return bookCopyRepository.findByEpcAndIsDeletedFalse(epc)
                .orElseThrow(() -> new BookCopyNotFoundException("BookCopy not found by EPC: " + epc));
    }

    @Override
    public void updateStatus(BookCopy bookCopy) {
        bookCopy.setTaken(false);
        bookCopyRepository.save(bookCopy);
    }

    @Override
    public long count() {
        return bookCopyRepository.count();
    }
}
