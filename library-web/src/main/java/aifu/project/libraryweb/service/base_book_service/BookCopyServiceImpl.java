package aifu.project.libraryweb.service.base_book_service;

import aifu.project.common_domain.dto.BookCopyStats;
import aifu.project.common_domain.dto.live_dto.BookCopyCreateDTO;
import aifu.project.common_domain.dto.live_dto.BookCopyResponseDTO;
import aifu.project.common_domain.dto.live_dto.BookCopyShortDTO;
import aifu.project.common_domain.dto.live_dto.BookCopySummaryDTO;
import aifu.project.common_domain.entity.BaseBook;
import aifu.project.common_domain.entity.BookCopy;
import aifu.project.common_domain.exceptions.BaseBookNotFoundException;
import aifu.project.common_domain.exceptions.BookCopyIsTakenException;
import aifu.project.common_domain.exceptions.BookCopyNotFoundException;
import aifu.project.common_domain.mapper.BookCopyMapper;
import aifu.project.common_domain.dto.ResponseMessage;
import aifu.project.libraryweb.repository.BaseBookRepository;
import aifu.project.libraryweb.repository.BookCopyRepository;
import aifu.project.libraryweb.utils.Util;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class BookCopyServiceImpl implements BookCopyService {
    private final BookCopyRepository bookCopyRepository;
    private final BaseBookRepository baseBookRepository;

    private static final String DEFAULT = "default";

    @Override
    public void saveAll(List<BookCopy> bookCopiesToSave) {
        bookCopyRepository.saveAll(bookCopiesToSave);

        log.info("Excel orqali qo'shilgan BookCopy lar saqlandi: {}",
                bookCopiesToSave.stream().map(BookCopy::getId).toList());
    }

    @Override
    public List<BookCopy> saveBookCopies(BaseBook baseBook, List<String> inventoryNumbers, List<String> errorMessages, int index) {
        Set<String> numbers = bookCopyRepository.existsInventoryNumbers(inventoryNumbers);
        List<BookCopy> newBookCopies = new ArrayList<>();
        boolean success = true;

        for (String inventoryNumber : inventoryNumbers) {
            if (numbers.contains(inventoryNumber)) {
                log.error("Excel orqali book copy qo'shish. InventoryNumber allaqachon mavjud: {}", inventoryNumber);

                errorMessages.add("Xatolik (%d - qatorda) yuz berdi. Sabab: '%s' kiritilgan inventar raqam raqam bazada mavjud. Iltimos boshqa inventar raqam kiriting"
                        .formatted(index, inventoryNumber));

                success = false;
            } else {
                BookCopy bookCopy = new BookCopy();
                bookCopy.setInventoryNumber(inventoryNumber);
                bookCopy.setBook(baseBook);
                bookCopy.setDeleted(false);
                bookCopy.setTaken(false);

                newBookCopies.add(bookCopy);
            }
        }

        return success ? newBookCopies : null;
    }

    @Override
    @Transactional
    public ResponseEntity<ResponseMessage> create(BookCopyCreateDTO dto) {
        log.info("BookCopy yaratish jarayoni boshlandi...");
        log.info("BookCopy yaratish uchun so'rov keldi: {}", dto);

        BaseBook baseBook = baseBookRepository.findByIdAndIsDeletedFalse(dto.baseBookId())
                .orElseThrow(() -> new BaseBookNotFoundException(dto.baseBookId()));

        String inventoryNumber = dto.inventoryNumber().trim();
        if (bookCopyRepository.existsByInventoryNumberAndIsDeletedFalse(inventoryNumber)) {
            throw new IllegalArgumentException("'%s' inventar raqamiga ega bo'lgan kitob nusxasi mavjud".formatted(inventoryNumber));
        }

        String epc = dto.epc() == null ? null : dto.epc().trim();
        if (epc != null && bookCopyRepository.existsByEpcAndIsDeletedFalse(epc)) {
            throw new IllegalArgumentException("'%s' EPC ega bo'lgan kitob nusxasi mavjud".formatted(epc));
        }

        BookCopy entity = BookCopyMapper.toEntity(dto, baseBook);
        entity = bookCopyRepository.save(entity);

        log.info("BookCopy yaratildi: {}", entity);
        log.info("BookCopy yaratilish jarayoni tugadi");

        BookCopyResponseDTO responseDTO = BookCopyMapper.toResponseDTO(entity);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ResponseMessage(true, "Kitob nusxasi muvaffaqiyatli yaratildi", responseDTO));
    }

    @Override
    @Transactional
    public ResponseEntity<ResponseMessage> update(Integer id, Map<String, Object> updates) {
        log.info("BookCopy tahrirlash jarayoni boshlandi...");
        log.info("BookCopy tahrirlash uchun so'rov keldi. ID: {}, Updates: {}", id, updates.keySet());

        BookCopy bookCopy = bookCopyRepository.findByIdAndIsDeletedFalse(id)
                .orElseThrow(() -> new BookCopyNotFoundException(BookCopyNotFoundException.BY_ID + id));

        updates.forEach((key, value) -> {

            if (value == null)
                throw new IllegalArgumentException("BookCopy tahrirlash. Value null");

            if (key == null) {
                throw new IllegalArgumentException("BookCopy tahrirlash. Key null");
            }

            switch (key) {
                case "inventoryNumber" -> {
                    String inventoryNumber = ((String) value).trim();
                    if (bookCopyRepository.existsByInventoryNumberAndIsDeletedFalse(inventoryNumber)) {
                        throw new IllegalArgumentException("Bu inventoryNumber bilan nusxa mavjud: " + inventoryNumber);
                    }

                    bookCopy.setInventoryNumber(inventoryNumber);
                }
                case "epc" -> {
                    String epc = ((String) value).trim();
                    if (bookCopyRepository.existsByEpcAndIsDeletedFalse(epc)) {
                        throw new IllegalArgumentException("Bu Epc bilan nusxa mavjud: " + epc);

                    }
                }
                case "shelfLocation" -> bookCopy.setShelfLocation(((String) value).trim());
                case "notes" -> bookCopy.setNotes(((String) value).trim());
                case "book" -> {
                    Integer baseBookId = Integer.parseInt(((String) value).trim());
                    BaseBook baseBook = baseBookRepository.findByIdAndIsDeletedFalse(baseBookId)
                            .orElseThrow(() -> new BaseBookNotFoundException(baseBookId));

                    bookCopy.setBook(baseBook);
                }
                default ->
                        throw new IllegalArgumentException("BookCopy tahrirlash. Mavjud bo'lmagan field. Field: " + key);
            }
        });
        bookCopyRepository.save(bookCopy);

        log.info("BookCopy tahrirladni: {}.\nTahrirlangan field lar: {}", bookCopy, updates.keySet());

        BookCopyResponseDTO responseDTO = BookCopyMapper.toResponseDTO(bookCopy);

        log.info("BookCopy tahrirlash jarayoni tugadi");

        return ResponseEntity.ok(new ResponseMessage(true, "Kitob nusxasi muvaffaqiyatli tahrirlandi", responseDTO));
    }

    @Override
    public ResponseEntity<ResponseMessage> get(Integer id) {
        log.info("ID: {} bo'yicha BookCopy ma'lumotlarini olish jarayoni boshlandi...", id);

        BookCopy bookCopy = bookCopyRepository.findByIdAndIsDeletedFalse(id)
                .orElseThrow(() -> new BookCopyNotFoundException(BookCopyNotFoundException.BY_ID + id));

        BookCopySummaryDTO response = BookCopySummaryDTO.toDTO(bookCopy);

        log.info("ID bo'yicha BookCopy ma'lumotlari olindi: {}", response);
        log.info("ID oraqli BookCopy malumotlarni olish jarayoni tugadi");

        return ResponseEntity.ok(new ResponseMessage(true, "Nusxa ma'lumotlari", response));
    }

    @Override
    public ResponseEntity<ResponseMessage> getByQuery(String field, String query) {
        log.info("Query orqali BookCopy ma'lumotlarini olish jarayoni boshlandi... Field: {}, Query: {}", field, query);
        BookCopy bookCopy = (field.equals("epc"))
                ? bookCopyRepository.findByEpcAndIsDeletedFalse(query).orElseThrow(() -> new BookCopyNotFoundException("EPC bo'yicha BookCopy topilmadi: " + field))
                : bookCopyRepository.findByInventoryNumberAndIsDeletedFalse(query).orElseThrow(() -> new BookCopyNotFoundException("Inventory number bo'yicha BookCopy topilmadi: " + field));

        BookCopySummaryDTO response = BookCopySummaryDTO.toDTO(bookCopy);

        log.info("{} bo'yicha BookCopy ma'lumotlari olindi: {}", field, response);
        log.info("Query orqali BookCopy malumotlarni olish jarayoni tugadi");

        return ResponseEntity.ok(new ResponseMessage(true, "BookCopy", response));
    }

    @Override
    public ResponseEntity<ResponseMessage> checkInventoryNumber(String inventoryNumber) {
        log.info("InventoryNumber tekshirish jarayoni boshlandi... InventoryNumber: {}", inventoryNumber);
        boolean exists = bookCopyRepository.existsByInventoryNumberAndIsDeletedFalse(inventoryNumber);

        log.info("Inventory number tekshirildi: {}, Status: {}", inventoryNumber, exists ? "Mavjud" : "Mavjud emas");
        log.info("Inventory number tekshirish jarayoni tugadi");

        return ResponseEntity.ok(
                new ResponseMessage(
                        true,
                        exists ? "Inventar raqam tizimda mavjud." : "Inventar raqam mavjud emas.",
                        exists));
    }

    @Override
    public ResponseEntity<ResponseMessage> getAll(String query, String field, String filter, int pageNumber, int pageSize, String sortDirection) {
        log.info("BookCopy ro'yxatini olish jarayoni boshlandi... Field: {}, Query: {}, Filter: {}, Sahifa: {}, Hajmi: {}, Tartiblash: {}",
                field, query, filter, pageNumber, pageSize, sortDirection);

        field = field == null ? DEFAULT : field;
        if (!field.equals(DEFAULT) && query == null) {
            throw new IllegalArgumentException("Query null bolishi mumkin emas. Field: " + field);
        }

        Sort.Direction direction = sortDirection.equalsIgnoreCase("asc") ? Sort.Direction.ASC : Sort.Direction.DESC;
        Pageable pageable = PageRequest.of(--pageNumber, pageSize, Sort.by(direction, "id"));

        String filterQuery = switch (filter) {
            case "inactive" -> "INACTIVE";
            case "active" -> "ACTIVE";
            default -> "ALL";
        };

        Page<BookCopyShortDTO> page = switch (field) {
            case "book" ->
                    bookCopyRepository.findByBookIdAndIsDeletedFalse(Integer.parseInt(query), pageable, filterQuery);

            case "inventoryNumber" ->
                    bookCopyRepository.findByInventoryNumberAndIsDeletedFalse(query, pageable, filterQuery);

            case "fullInfo" -> {
                String[] parts = query.trim().split("~");

                String first = parts[0].isBlank() ? null : "%" + parts[0].toLowerCase() + "%";
                String second = (parts.length == 2) ? "%" + parts[1].toLowerCase() + "%" : null;

                yield bookCopyRepository.findByTitleAndAuthor(first, second, pageable, filterQuery);
            }

            case "epc" -> bookCopyRepository.findByEpcAndIsDeletedFalse(query, pageable, filterQuery);

            case DEFAULT -> bookCopyRepository.findByIsDeletedFalse(pageable, filterQuery);

            default -> throw new IllegalArgumentException("Noto'g'ri qidiruv maydoni: " + field);
        };

        List<BookCopyShortDTO> content = page.getContent();

        log.info("BookCopy ro'yxati olindi (Search). Sahifa: {}, Hajmi: {}, Tartiblash: {}",
                page.getNumber() + 1, page.getSize(), sortDirection);
        log.info("BookCopy ro'yxati: {}", content.stream().map(BookCopyShortDTO::id).toList());

        Map<String, Object> map = Util.getPageInfo(page);
        map.put("list", content);

        log.info("BookCopy ro'yxatini olish jarayoni tugadi");

        return ResponseEntity.ok(new ResponseMessage(true, "BookCopy ro'yxati", map));
    }

    @Override
    @Transactional
    public ResponseEntity<ResponseMessage> delete(Integer id) {
        log.info("ID: {} bo'lgan BookCopy o'chirish jarayoni boshlandi...", id);
        BookCopy bookCopy = bookCopyRepository.findByIdAndIsDeletedFalse(id)
                .orElseThrow(() -> new BookCopyNotFoundException(BookCopyNotFoundException.BY_ID + id));

        if (bookCopy.isTaken())
            throw new BookCopyIsTakenException("BookCopy Student da. Id: " + bookCopy.getId());

        bookCopy.setDeleted(true);
        bookCopyRepository.save(bookCopy);

        log.info("ID: {} bo'lgan BookCopy o'chirildi.", id);
        log.info("ID orqali BookCopy o'chirish jarayoni tugadi");

        return ResponseEntity.ok(new ResponseMessage(true, "BookCopy o'chirildi", id));
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
    public BookCopy findById(Integer id) {
        return bookCopyRepository.findById(id)
                .orElseThrow(() -> new BookCopyNotFoundException(BookCopyNotFoundException.BY_ID + id));
    }

    @Override
    public void updateStatus(BookCopy bookCopy, boolean isTaken) {
        log.info("BookCopy statusini yangilash jarayoni boshlandi... ID: {}, Taken: {}", bookCopy.getId(), isTaken);
        bookCopy.setTaken(isTaken);
        bookCopyRepository.save(bookCopy);

        log.info("BookCopy status yangilandi. ID: {}, Taken: {}", bookCopy.getId(), isTaken);
        log.info("BookCopy statusini yangilash jarayoni tugadi");
    }

    @Override
    public long count() {
        return bookCopyRepository.count();
    }

    @Override
    public List<String> findByBaseBookId(Integer id) {
        return bookCopyRepository.findInventoryNumberByBook_IdAndIsDeletedFalse(id);
    }
}
