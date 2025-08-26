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
    public void saveBookCopies(BaseBook baseBook, List<String> inventoryNumbers) {
        Set<String> numbers = bookCopyRepository.existsInventoryNumbers(inventoryNumbers);
        List<String> existsInventoryNumbers = new ArrayList<>();
        List<BookCopy> newBookCopies = new ArrayList<>();

        for (String inventoryNumber : inventoryNumbers) {
            if (numbers.contains(inventoryNumber)) {
                existsInventoryNumbers.add(inventoryNumber);
            } else {
                BookCopy bookCopy = new BookCopy();
                bookCopy.setInventoryNumber(inventoryNumber);
                bookCopy.setBook(baseBook);
                bookCopy.setDeleted(false);
                bookCopy.setTaken(false);

                newBookCopies.add(bookCopy);
            }
        }

        if (!newBookCopies.isEmpty()) {
            bookCopyRepository.saveAll(newBookCopies);
        }
    }

    @Override
    public ResponseEntity<ResponseMessage> create(BookCopyCreateDTO dto) {
        BaseBook baseBook = baseBookRepository.findByIdAndIsDeletedFalse(dto.getBaseBookId())
                .orElseThrow(() -> new BaseBookNotFoundException(dto.getBaseBookId()));

        String inventoryNumber = dto.getInventoryNumber();
        if (bookCopyRepository.existsByInventoryNumberAndIsDeletedFalse(inventoryNumber)) {
            throw new IllegalArgumentException("Bu inventoryNumber bilan BookCopy mavjud: " + inventoryNumber);
        }

        String epc = dto.getEpc();
        if (epc != null && bookCopyRepository.existsByEpcAndIsDeletedFalse(epc)) {
            throw new IllegalArgumentException("Bu Epc bilan BookCopy mavjud: " + epc);
        }

        BookCopy entity = BookCopyMapper.toEntity(dto, baseBook);
        entity = bookCopyRepository.save(entity);

        log.info("BookCopy yaratildi: {}", entity);

        BookCopyResponseDTO responseDTO = BookCopyMapper.toResponseDTO(entity);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ResponseMessage(true, "BookCopy muvaffaqiyatli yaratildi", responseDTO));
    }

    @Override
    public ResponseEntity<ResponseMessage> update(Integer id, Map<String, Object> updates) {
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
                    String inventoryNumber = (String) value;
                    if (bookCopyRepository.existsByInventoryNumberAndIsDeletedFalse(inventoryNumber)) {
                        throw new IllegalArgumentException("Bu inventoryNumber bilan BookCopy mavjud: " + inventoryNumber);
                    }

                    bookCopy.setInventoryNumber(inventoryNumber);
                }
                case "shelfLocation" -> bookCopy.setShelfLocation((String) value);
                case "notes" -> bookCopy.setNotes((String) value);
                case "book" -> {
                    Integer baseBookId = Integer.parseInt((String) value);
                    BaseBook baseBook = baseBookRepository.findByIdAndIsDeletedFalse(baseBookId)
                            .orElseThrow(() -> new BaseBookNotFoundException(baseBookId));

                    bookCopy.setBook(baseBook);
                }
                default ->
                        throw new IllegalArgumentException("BookCopy tahrirlash. Mavjud bo'lmagan field. Field: " + key);
            }
        });

        log.info("BookCopy tahrirladni: {}.\nTahrirlangan field lar: {}", bookCopy, updates.keySet());

        bookCopyRepository.save(bookCopy);

        BookCopyResponseDTO responseDTO = BookCopyMapper.toResponseDTO(bookCopy);
        return ResponseEntity.ok(new ResponseMessage(true, "BookCopy muvaffaqiyatli tahrirlandi", responseDTO));
    }

    @Override
    public ResponseEntity<ResponseMessage> get(Integer id) {
        BookCopy bookCopy = bookCopyRepository.findByIdAndIsDeletedFalse(id)
                .orElseThrow(() -> new BookCopyNotFoundException(BookCopyNotFoundException.BY_ID + id));

        BookCopySummaryDTO response = BookCopySummaryDTO.toDTO(bookCopy);

        log.info("EPC bo'yicha BookCopy ma'lumotlari olindi: {}", response);

        return ResponseEntity.ok(new ResponseMessage(true, "BookCopy", response));
    }

    @Override
    public ResponseEntity<ResponseMessage> getByEPC(String epc) {
//        BookCopy bookCopy = bookCopyRepository.findByEpcAndIsDeletedFalse(epc)
//                .orElseThrow(() -> new BookCopyNotFoundException("EPC bo'yicha BookCopy topilmadi: " + epc));
        BookCopy bookCopy = bookCopyRepository.findByInventoryNumberAndIsDeletedFalse(epc)
                .orElseThrow(() -> new BookCopyNotFoundException("Inventory number bo'yicha BookCopy topilmadi: " + epc));

        BookCopySummaryDTO response = BookCopySummaryDTO.toDTO(bookCopy);

        log.info("EPC bo'yicha BookCopy ma'lumotlari olindi: {}", response);

        return ResponseEntity.ok(new ResponseMessage(true, "BookCopy", response));
    }

    @Override
    public ResponseEntity<ResponseMessage> checkInventoryNumber(String inventoryNumber) {
        boolean exists = bookCopyRepository.existsByInventoryNumberAndIsDeletedFalse(inventoryNumber);

        log.info("Inventory number tekshirildi: {}, Status: {}", inventoryNumber, exists ? "Mavjud" : "Mavjud emas");

        return ResponseEntity.ok(
                new ResponseMessage(
                        true,
                        exists ? "Inventory number mavjud" : "Inventory number mavjud emas",
                        exists));
    }

    @Override
    public ResponseEntity<ResponseMessage> getAll(String query, String field, int pageNumber, int pageSize, String sortDirection) {
        field = field == null ? DEFAULT : field;
        if (!field.equals(DEFAULT) && query == null) {
            throw new IllegalArgumentException("Query null bolishi mumkin emas. Field: " + field);
        }

        Sort.Direction direction = sortDirection.equalsIgnoreCase("asc") ? Sort.Direction.ASC : Sort.Direction.DESC;
        Pageable pageable = PageRequest.of(--pageNumber, pageSize, Sort.by(direction, "id"));

        Page<BookCopyShortDTO> page = switch (field) {
            case "book" -> bookCopyRepository.findByBookIdAndIsDeletedFalse(Integer.parseInt(query), pageable);

            case "inventoryNumber" -> bookCopyRepository.findByInventoryNumberAndIsDeletedFalse(query, pageable);

            case "fullInfo" -> {
                String[] parts = query.trim().split("\\s+");

                String first = "%" + parts[0].toLowerCase() + "%";
                String second = (parts.length == 2) ? "%" + parts[1].toLowerCase() + "%" : null;

                yield bookCopyRepository.findByTitleAndAuthor(first, second, pageable);
            }

            case "epc" -> bookCopyRepository.findByEpcAndIsDeletedFalse(query, pageable);

            case DEFAULT -> bookCopyRepository.findByIsDeletedFalse(pageable);

            default -> throw new IllegalArgumentException("Noto'g'ri qidiruv maydoni: " + field);
        };

        List<BookCopyShortDTO> content = page.getContent();

        log.info("BookCopy ro'yxati olindi (Search). Sahifa: {}, Hajmi: {}, Tartiblash: {}",
                page.getNumber() + 1, page.getSize(), sortDirection);
        log.info("BookCopy ro'yxati: {}", content.stream().map(BookCopyShortDTO::id).toList());


        Map<String, Object> map = Util.getPageInfo(page);
        map.put("list", content);

        return ResponseEntity.ok(new ResponseMessage(true, "BookCopy ro'yxati", map));
    }

    @Override
    public ResponseEntity<ResponseMessage> delete(Integer id) {
        BookCopy bookCopy = bookCopyRepository.findByIdAndIsDeletedFalse(id)
                .orElseThrow(() -> new BookCopyNotFoundException(BookCopyNotFoundException.BY_ID + id));

        if (bookCopy.isTaken())
            throw new BookCopyIsTakenException("BookCopy Student da. Id: " + bookCopy.getId());

        bookCopy.setDeleted(true);
        bookCopyRepository.save(bookCopy);

        log.info("ID: {} bo'lgan BookCopy o'chirildi.", id);

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
    public BookCopy findByEpc(String epc) {
        return bookCopyRepository.findByEpcAndIsDeletedFalse(epc)
                .orElseThrow(() -> new BookCopyNotFoundException("{} -> EPC ega BookCopy topilmadi: " + epc));
    }

    @Override
    public BookCopy findByInventoryNumber(String inventoryNumber) {
        return bookCopyRepository.findByInventoryNumberAndIsDeletedFalse(inventoryNumber)
                .orElseThrow(() -> new BookCopyNotFoundException("Inventory number bo'yicha BookCopy topilmadi: " + inventoryNumber));
    }

    @Override
    public void updateStatus(BookCopy bookCopy, boolean isTaken) {
        bookCopy.setTaken(isTaken);
        bookCopyRepository.save(bookCopy);

        log.info("BookCopy status yangilandi. ID: {}, Taken: {}", bookCopy.getId(), isTaken);
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
