package aifu.project.libraryweb.service.base_book_service;


import aifu.project.common_domain.dto.BaseBookShortDTO;
import aifu.project.common_domain.dto.BookCopyStats;
import aifu.project.common_domain.dto.live_dto.BaseBookCategoryDTO;
import aifu.project.common_domain.dto.live_dto.BaseBookCreateDTO;
import aifu.project.common_domain.dto.live_dto.BaseBookResponseDTO;
import aifu.project.common_domain.entity.BaseBook;
import aifu.project.common_domain.entity.BaseBookCategory;
import aifu.project.common_domain.entity.BookCopy;
import aifu.project.common_domain.exceptions.BaseBookCategoryNotFoundException;
import aifu.project.common_domain.exceptions.BaseBookNotFoundException;
import aifu.project.common_domain.exceptions.BookCopyIsTakenException;
import aifu.project.common_domain.mapper.BaseBookMapper;
import aifu.project.common_domain.payload.ResponseMessage;
import aifu.project.libraryweb.lucene.LuceneIndexService;
import aifu.project.libraryweb.repository.BaseBookCategoryRepository;
import aifu.project.libraryweb.repository.BaseBookRepository;
import aifu.project.libraryweb.utils.Util;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class BaseBookServiceImpl implements BaseBookService {

    private final BaseBookRepository baseBookRepository;
    private final BookCopyService bookCopyService;
    private final BaseBookCategoryRepository categoryRepository;
    private final LuceneIndexService luceneIndexService;

    @Override
    public ResponseEntity<ResponseMessage> create(BaseBookCreateDTO dto) {
        BaseBookCategory category = categoryRepository
                .findByIdAndIsDeletedFalse(dto.getCategoryId())
                .orElseThrow(() -> new BaseBookCategoryNotFoundException(dto.getCategoryId()));

        BaseBook entity = baseBookRepository.save(BaseBookMapper.toEntity(dto, category));
        BaseBookResponseDTO responseDTO = BaseBookMapper.toResponseDTO(entity);

        log.info("Base book created: {}", entity);

        try {
            luceneIndexService.indexBaseBooks(entity);
        } catch (IOException e) {
            log.error(e.getMessage());
        }

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ResponseMessage(true, "Base book create successfully", responseDTO));
    }

    @Override
    public ResponseEntity<ResponseMessage> getAll(int pageNumber, int pageSize) {
        Pageable pageable = PageRequest.of(--pageNumber, pageSize, Sort.by(Sort.Direction.ASC, "id"));
        Page<BaseBook> page = baseBookRepository.findByIsDeletedFalse(pageable);

        List<BaseBookShortDTO> list = createBaseBookShortDTO(page.getContent());

        Map<String, Object> map = Util.getPageInfo(page);
        map.put("data", list);

        return ResponseEntity.ok(new ResponseMessage(true, "Base book list", map));
    }

    @Override
    public ResponseEntity<ResponseMessage> getOne(Integer id) {
        BaseBook entity = baseBookRepository.findByIdAndIsDeletedFalse(id)
                .orElseThrow(() -> new BaseBookNotFoundException(id));

        BaseBookResponseDTO dto = BaseBookMapper.toResponseDTO(entity);
        Map<String, Long> mapCount = bookCopyService.getTotalAndTakenCount(entity.getId());

        Map<String, Object> map = Map.of(
                "book", dto,
                "totalCount", mapCount.get("total"),
                "takenCount", mapCount.get("taken")
        );

        return ResponseEntity.ok(new ResponseMessage(true, "Base book by id: " + id, map));
    }

    @Override
    public ResponseEntity<ResponseMessage> update(Integer id, Map<String, Object> updates) {
        BaseBook entity = baseBookRepository.findByIdAndIsDeletedFalse(id)
                .orElseThrow(() -> new BaseBookNotFoundException(id));

        for (Map.Entry<String, Object> entry : updates.entrySet()) {
            String key = entry.getKey();
            Object value = entry.getValue();

            if (key == null)
                throw new IllegalArgumentException("Key is null");

            if (value == null)
                throw new IllegalArgumentException("Value is null");

            switch (key) {
                case "title" -> entity.setTitle((String) value);
                case "author" -> entity.setAuthor((String) value);
                case "series" -> entity.setSeries((String) value);
                case "titleDetails" -> entity.setTitleDetails((String) value);
                case "publicationYear" -> entity.setPublicationYear(Integer.parseInt(value.toString()));
                case "publisher" -> entity.setPublisher((String) value);
                case "publicationCity" -> entity.setPublicationCity((String) value);
                case "isbn" -> entity.setIsbn((String) value);
                case "pageCount" -> entity.setPageCount(Integer.parseInt(value.toString()));
                case "language" -> entity.setLanguage((String) value);
                case "udc" -> entity.setUdc((String) value);
                case "category" -> {
                    Integer categoryId = Integer.parseInt(value.toString());
                    BaseBookCategory category = categoryRepository.findByIdAndIsDeletedFalse(categoryId)
                            .orElseThrow(() -> new BaseBookCategoryNotFoundException(categoryId));
                    entity.setCategory(category);
                }
                default -> throw new IllegalArgumentException("Invalid field: " + key);
            }
        }

        baseBookRepository.save(entity);

        log.info("Base book updated: {}.\nUpdated fields: {}", entity, updates.keySet());

        try {
            luceneIndexService.indexBaseBooks(entity);
        } catch (IOException e) {
            log.error(e.getMessage());
        }

        BaseBookResponseDTO responseDTO = BaseBookMapper.toResponseDTO(entity);
        return ResponseEntity.ok(new ResponseMessage(true, "Successfully updated", responseDTO));
    }


    @Override
    public ResponseEntity<ResponseMessage> delete(Integer id) {
        BaseBook entity = baseBookRepository.findByIdAndIsDeletedFalse(id)
                .orElseThrow(() -> new BaseBookNotFoundException(id));

        boolean canDelete = entity.getCopies()
                .stream()
                .allMatch(BookCopy::isDeleted);

        if (!canDelete)
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ResponseMessage(true, "Cannot be deleted. Copies of the book are available", id));

        entity.setDeleted(true);
        baseBookRepository.save(entity);

        log.info("Base book deleted by id: {}", id);

        try {
            luceneIndexService.deleteBaseBookIndex(id);
        } catch (IOException e) {
            log.error(e.getMessage());
        }

        return ResponseEntity.ok(new ResponseMessage(true, "Base book deleted successfully", id));
    }

    public ResponseEntity<ResponseMessage> deleteByCategory(Integer categoryId) {
        if (!categoryRepository.existsByIdAndIsDeletedFalse(categoryId)) {
            throw new BaseBookCategoryNotFoundException(categoryId);
        }

        List<BaseBook> books = baseBookRepository.findByCategory_IdAndIsDeletedFalse(categoryId);

        boolean canDeleteAll = books.stream()
                .allMatch(book -> book.getCopies().stream()
                        .allMatch(copy -> copy.isDeleted() && !copy.isTaken())
                );

        if (!canDeleteAll) {
            throw new BookCopyIsTakenException("Some copies of the books are still active or in the hands of users.");
        }

        for (BaseBook book : books) {
            book.setDeleted(true);
        }
        baseBookRepository.saveAll(books);

        log.info("All Base book deleted by Category id: {}. BaseBook list: {}", categoryId, books);

        return ResponseEntity.ok(new ResponseMessage(true, "Books in category successfully deleted", null));
    }

    @Override
    public ResponseEntity<ResponseMessage> getByCategory(Integer categoryId, int pageNumber, int pageSize) {
        BaseBookCategory category = categoryRepository.findByIdAndIsDeletedFalse(categoryId)
                .orElseThrow(() -> new BaseBookCategoryNotFoundException(categoryId));

        Pageable pageable = PageRequest.of(--pageNumber, pageSize, Sort.by(Sort.Direction.ASC, "id"));

        Page<BaseBook> page = baseBookRepository.findAllByCategoryAndIsDeletedFalse(category, pageable);

        List<BaseBookShortDTO> list = createBaseBookShortDTO(page.getContent());

        Map<String, Object> map = Util.getPageInfo(page);
        map.put("data", list);

        return ResponseEntity.ok(new ResponseMessage(true, "Base book list. By categoryId: " + categoryId, map));
    }

    @Override
    public long countBooks() {
        return baseBookRepository.count();
    }

    private List<BaseBookShortDTO> createBaseBookShortDTO(List<BaseBook> books) {
        List<Integer> bookIds = books.stream().map(BaseBook::getId).toList();
        Map<Integer, BookCopyStats> statsMap = bookCopyService.getStatsMap(bookIds);

        return books.stream()
                .map(book -> {
                    BookCopyStats stats = statsMap.getOrDefault(book.getId(), new BookCopyStats(0L, 0L, book.getId()));
                    return new BaseBookShortDTO(
                            book.getId(),
                            book.getTitle(),
                            book.getAuthor(),
                            new BaseBookCategoryDTO(
                                    book.getCategory().getId(),
                                    book.getCategory().getName()
                            ),
                            book.getIsbn(),
                            stats.total(),
                            stats.taken()
                    );
                })
                .toList();
    }

}
