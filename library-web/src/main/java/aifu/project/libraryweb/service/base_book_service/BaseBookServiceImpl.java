package aifu.project.libraryweb.service.base_book_service;

import aifu.project.common_domain.dto.excel_dto.BookExcelDTO;
import aifu.project.common_domain.dto.live_dto.*;
import aifu.project.common_domain.dto.BookCopyStats;
import aifu.project.common_domain.entity.BaseBook;
import aifu.project.common_domain.entity.BaseBookCategory;
import aifu.project.common_domain.entity.BookCopy;
import aifu.project.common_domain.exceptions.BaseBookCategoryNotFoundException;
import aifu.project.common_domain.exceptions.BaseBookNotFoundException;
import aifu.project.common_domain.mapper.BaseBookMapper;
import aifu.project.common_domain.dto.ResponseMessage;
import aifu.project.libraryweb.lucene.LuceneIndexService;
import aifu.project.libraryweb.repository.BaseBookCategoryRepository;
import aifu.project.libraryweb.repository.BaseBookRepository;
import aifu.project.libraryweb.utils.Util;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

import static aifu.project.libraryweb.utils.UpdateUtils.updateIfChanged;

@Slf4j
@Service
@RequiredArgsConstructor
public class BaseBookServiceImpl implements BaseBookService {

    private final BaseBookRepository baseBookRepository;
    private final BookCopyService bookCopyService;
    private final BaseBookCategoryRepository categoryRepository;
    private final LuceneIndexService luceneIndexService;

    private static final String DEFAULT = "default";

    @Override
    public ResponseEntity<ResponseMessage> getOptions() {
        List<BaseBookOptionsDTO> data = baseBookRepository.findAllOptionsData();
        return ResponseEntity.ok(new ResponseMessage(true, "Kitoblar ro'yxati", data));
    }

    @Override
    @Transactional
    public ResponseEntity<ResponseMessage> importFromExcel(MultipartFile file) {
        List<BookImportDTO> bookImportDTOS = ExcelBookHelper.excelToBooks(file);

        return saveBooks(bookImportDTOS);
    }

    @Override
    public ResponseEntity<byte[]> templateFromExcel() {
        byte[] bytes = ExcelBookHelper.templateExcel();
        return bytes.length > 0
                ? ResponseEntity
                .ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=Shablon.xlsx")
                .body(bytes)
                : ResponseEntity.noContent().build();
    }

    private ResponseEntity<ResponseMessage> saveBooks(List<BookImportDTO> bookImportDTOS) {
        int index = 1;
        boolean success = true;
        List<String> errorMessages = new ArrayList<>();
        List<BaseBook> baseBooksToSave = new ArrayList<>();
        List<BookCopy> bookCopiesToSave = new ArrayList<>();

        Map<String, BaseBookCategory> categoryMap = categoryRepository.findAllActive()
                .stream()
                .collect(Collectors.toMap(
                        c -> c.getName().toLowerCase(),
                        Function.identity()
                ));

        for (BookImportDTO dto : bookImportDTOS) {
            ++index;

            BaseBookCategory category = getCategory(categoryMap, dto.category(), errorMessages, index);

            if (category == null) success = false;

            BaseBook baseBook = BookImportDTO.createBaseBook(dto, category);

            baseBooksToSave.add(baseBook);

            log.info("Excel orqali base book qo'shildi\nBaseBook: {}.Bazada yaratilmadi...", baseBook);

            List<BookCopy> newBookCopies = bookCopyService.saveBookCopies(baseBook, dto.inventoryNumbers(), errorMessages, index);

            if (newBookCopies == null) {
                success = false;
            } else {
                bookCopiesToSave.addAll(newBookCopies);
            }

        }

        if (success) {
            baseBookRepository.saveAll(baseBooksToSave);

            log.info("Excel orqali BaseBook qo'shildi: {}", baseBooksToSave.stream().map(BaseBook::getId).toList());

            if (!bookCopiesToSave.isEmpty()) {
                bookCopyService.saveAll(bookCopiesToSave);
                log.info("Excel orqali BookCopy qo'shildi: {}", bookCopiesToSave.stream().map(BookCopy::getId).toList());
            }

            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(new ResponseMessage(true, "Muvaffaqiyatli qo'shildi", null));
        }

        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(new ResponseMessage(true, "Xatolik yuz berdi", errorMessages));
    }

    private BaseBookCategory getCategory(Map<String, BaseBookCategory> categoryMap, String inputCategoryName, List<String> errorMessages, int index) {
        String categoryName = inputCategoryName == null ? "" : inputCategoryName.toLowerCase();

        if (categoryMap.containsKey(categoryName)) {
            return categoryMap.get(categoryName);
        } else {
            log.error("Base bookni excel orqali qo'shishda BaseBookCategory topilmadi: {}", categoryName);

            errorMessages.add("Xatolik (%d - qatorda) yuz berdi. Sabab: mavjud bo'lmagan Kategoriya kiritildi. Kiritilgan Kategoriya: %s"
                    .formatted(index, inputCategoryName));

            return null;
        }
    }

    @Override
    @Transactional
    public ResponseEntity<ResponseMessage> create(BaseBookCreateDTO dto) {
        log.info("BaseBook yarartilish jarayoni boshlandi...");
        log.info("BaseBook yaratish uchun kelgan DTO: {}", dto);

        Integer categoryId = dto.categoryId();

        BaseBookCategory category = categoryRepository
                .findByIdAndIsDeletedFalse(categoryId)
                .orElseThrow(BaseBookCategoryNotFoundException::new);

        BaseBook entity = baseBookRepository.save(BaseBookMapper.toEntity(dto, category));
        BaseBookResponseDTO responseDTO = BaseBookMapper.toResponseDTO(entity);

        log.info("Base book yaratildi: {}", entity);

        try {
            luceneIndexService.indexBaseBooks(entity);
        } catch (IOException e) {
            log.error(e.getMessage());
        }

        log.info("BaseBook yarartilish jarayoni yakunlandi");

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ResponseMessage(true, "Kitob muvaffaqiyatli yaratildi", responseDTO));
    }

    @Override
    public ResponseEntity<ResponseMessage> get(Integer id) {
        log.info("ID {} bo'yicha base book ma'lumotlarini olish jarayoni boshlandi...", id);

        BaseBook entity = baseBookRepository.findByIdAndIsDeletedFalse(id)
                .orElseThrow(() -> new BaseBookNotFoundException(id));

        BaseBookResponseDTO dto = BaseBookMapper.toResponseDTO(entity);
        Map<String, Long> mapCount = bookCopyService.getTotalAndTakenCount(entity.getId());

        log.info("ID {} bo'yicha base book ma'lumotlari olindi: {}", id, dto);

        Map<String, Object> map = Map.of(
                "book", dto,
                "totalCount", mapCount.get("total"),
                "takenCount", mapCount.get("taken")
        );

        log.info("ID {} bo'yicha base book ma'lumotlarini olish jarayoni yakunlandi", id);

        return ResponseEntity.ok(new ResponseMessage(true, "Kitob ma'lumotlari", map));
    }

    @Override
    @Transactional
    public ResponseEntity<ResponseMessage> update(Integer id, BaseBookUpdateDTO updates) {
        log.info("ID {} bo'yicha base book tahrirlash jarayoni boshlandi...", id);
        log.info("Update DTO: {}", updates);

        BaseBook entity = baseBookRepository.findByIdAndIsDeletedFalse(id)
                .orElseThrow(() -> new BaseBookNotFoundException(id));

        updateEntityFields(updates, entity);

        baseBookRepository.save(entity);

        log.info("Base book tahrirlandi: {}.", entity);

        try {
            luceneIndexService.indexBaseBooks(entity);
        } catch (IOException e) {
            log.error(e.getMessage());
        }

        log.info("ID {} bo'yicha base book tahrirlash jarayoni yakunlandi", id);

        BaseBookResponseDTO responseDTO = BaseBookMapper.toResponseDTO(entity);
        return ResponseEntity.ok(new ResponseMessage(true, "Kitob muvaffaqiyatli tahrirlandi", responseDTO));
    }

    private void updateEntityFields(BaseBookUpdateDTO updates, BaseBook entity) {
        updateIfChanged(updates.author(), entity::getAuthor, entity::setAuthor);
        updateIfChanged(updates.title(), entity::getTitle, entity::setTitle);
        updateIfChanged(updates.series(), entity::getSeries, entity::setSeries);
        updateIfChanged(updates.titleDetails(), entity::getTitleDetails, entity::setTitleDetails);
        updateIfChanged(updates.publicationYear(), entity::getPublicationYear, entity::setPublicationYear);
        updateIfChanged(updates.publisher(), entity::getPublisher, entity::setPublisher);
        updateIfChanged(updates.publicationCity(), entity::getPublicationCity, entity::setPublicationCity);
        updateIfChanged(updates.isbn(), entity::getIsbn, entity::setIsbn);
        updateIfChanged(updates.pageCount(), entity::getPageCount, entity::setPageCount);
        updateIfChanged(updates.language(), entity::getLanguage, entity::setLanguage);
        updateIfChanged(updates.udc(), entity::getUdc, entity::setUdc);

        if (updates.categoryId() != null && !Objects.equals(updates.categoryId(), entity.getCategory().getId())) {
            BaseBookCategory category = categoryRepository.findByIdAndIsDeletedFalse(updates.categoryId())
                    .orElseThrow(() -> new BaseBookCategoryNotFoundException(updates.categoryId()));
            entity.setCategory(category);
        }
    }

    @Override
    @Transactional
    public ResponseEntity<ResponseMessage> delete(Integer id) {
        log.info("ID {} bo'yicha base book o'chirish jarayoni boshlandi...", id);

        BaseBook entity = baseBookRepository.findByIdAndIsDeletedFalse(id)
                .orElseThrow(() -> new BaseBookNotFoundException(id));

        boolean canDelete = entity.getCopies()
                .stream()
                .allMatch(BookCopy::isDeleted);

        if (!canDelete) {
            log.warn("Base bookni o'chirishga urinish: {}, lekin book copy lar mavjud", id);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ResponseMessage(true, "Kitobni o'chirib bo'lmayidi. Kitob nusxalari mavjud", id));
        }

        entity.setDeleted(true);
        baseBookRepository.save(entity);

        log.info("Base book o'chirildi ID: {}", id);

        try {
            luceneIndexService.deleteBaseBookIndex(id);
        } catch (IOException e) {
            log.error(e.getMessage());
        }

        log.info("ID {} bo'yicha base book o'chirish jarayoni yakunlandi", id);

        return ResponseEntity.ok(new ResponseMessage(true, "Kitob muvaffaqiyatli o'chirildi", id));
    }

    @Override
    public ResponseEntity<ResponseMessage> getAll(String query, String field, int pageNumber, int pageSize, String sortDirection) {
        log.info("Base book ro'yxatini olish jarayoni boshlandi...");
        log.info("Base book ro'yxatini olish uchun kelgan parametlar: query='{}', field='{}', pageNumber={}, pageSize={}, sortDirection='{}'",
                query, field, pageNumber, pageSize, sortDirection);

        field = field != null ? field : DEFAULT;
        if ((!field.equals(DEFAULT)) && query == null) {
            throw new IllegalArgumentException("Query bo'sh bo'lishi mumkin emas. Field: " + field);
        }

        Sort.Direction direction = sortDirection.equals("desc") ? Sort.Direction.DESC : Sort.Direction.ASC;
        Pageable pageable = PageRequest.of(--pageNumber, pageSize, Sort.by(direction, "id"));

        Page<BaseBook> page = switch (field) {
            case "id" -> baseBookRepository.searchById(Long.parseLong(query), pageable);
            case "category" -> baseBookRepository.searchByCategory_Id(Integer.parseInt(query), pageable);
            case "fullInfo" -> {
                String[] parts = query.trim().split("~");

                String first = parts[0].isBlank() ? null : "%" + parts[0].toLowerCase() + "%";
                String second = (parts.length == 2) ? "%" + parts[1].toLowerCase() + "%" : null;

                yield baseBookRepository.searchByTitleAndAuthor(first, second, pageable);
            }
            case "isbn" -> baseBookRepository.searchByIsbn(query.trim(), pageable);
            case "udc" -> baseBookRepository.searchByUdc(query.trim(), pageable);
            case "series" -> baseBookRepository.searchSeries(query.trim(), pageable);
            case DEFAULT -> baseBookRepository.findByIsDeletedFalse(pageable);
            default -> throw new IllegalArgumentException("Mavjud bo'lmagan field: " + field);
        };

        List<BaseBookShortDTO> list = createBaseBookShortDTO(page.getContent());

        Map<String, Object> map = Util.getPageInfo(page);
        map.put("data", list);

        log.info("Base book ro'yxatini olish amalga oshirildi: query={}, field={}, pageNumber={}, pageSize={}", query, field, pageNumber + 1, pageSize);
        log.info("Base book ro'yxatini olish natijalari (ID): {}", list.stream().map(BaseBookShortDTO::id).toList());
        log.info("Base book ro'yxatini olish jarayoni yakunlandi");

        return ResponseEntity.ok(new ResponseMessage(true, "Kitoblar ro'yxati", map));
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
                            BaseBookCategoryDTO.toDTO(book.getCategory()),
                            book.getIsbn(),
                            stats.total(),
                            stats.taken()
                    );
                })
                .toList();
    }

    @Override
    public List<BookExcelDTO> getAllBooks() {
        List<BookExcelDTO> allBooks = baseBookRepository.getAllBooks();

        return allBooks.stream()
                .map(book -> {
                    List<String> inv = bookCopyService.findByBaseBookId(book.id());
                    return new BookExcelDTO(
                            book.id(),
                            book.author(),
                            book.title(),
                            book.category(),
                            book.series(),
                            book.titleDetail(),
                            book.publicationYear(),
                            book.publisher(),
                            book.publicationCity(),
                            book.isbn(),
                            book.pageCount(),
                            book.language(),
                            book.udc(),
                            (long) inv.size(),
                            inv
                    );
                })
                .toList();
    }

}
