package aifu.project.libraryweb.service.history_service;

import aifu.project.common_domain.dto.ResponseMessage;
import aifu.project.common_domain.dto.histroy_dto.HistoryShortDTO;
import aifu.project.common_domain.dto.histroy_dto.HistorySummaryDTO;
import aifu.project.common_domain.entity.Booking;
import aifu.project.common_domain.entity.History;
import aifu.project.common_domain.entity.Librarian;
import aifu.project.common_domain.exceptions.HistoryNotFoundException;
import aifu.project.libraryweb.entity.SecurityLibrarian;
import aifu.project.libraryweb.repository.HistoryRepository;
import aifu.project.libraryweb.utils.Util;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class HistoryServiceImpl implements HistoryService {
    private final HistoryRepository historyRepository;

    @Override
    public void add(Booking booking, Librarian librarian) {
        History history = new History();
        history.setUser(booking.getStudent());
        history.setBook(booking.getBook());
        history.setGivenAt(booking.getGivenAt());
        history.setDueDate(booking.getDueDate());
        history.setReturnedAt(LocalDate.now());
        history.setIssuedBy(booking.getIssuedBy());
        history.setReturnedBy(librarian);
        History save = historyRepository.save(history);

        log.info("Tarix qo'shildi: {}", save);
    }

    @Override
    public ResponseEntity<ResponseMessage> getById(Long id) {
        log.info("ID:{} orqali Tarixni olib chiqish jarayoni...", id);

        History history = historyRepository.findById(id)
                .orElseThrow(() -> new HistoryNotFoundException("History topilmadi: " + id));

        HistorySummaryDTO data = HistorySummaryDTO.toDTO(history);

        log.info("ID: {} orqali Tarixni olib chiqish jarayoni yakunlandi", id);

        return ResponseEntity.ok(new ResponseMessage(true, "Tarix ma'lumotlari muvaffaqiyatli qaytarildi", data));
    }

    @Override
    public ResponseEntity<ResponseMessage> getAll(String field, String query, int pageNumber, int pageSize, String sortDirection) {
        log.info("Tarix ro'yxatini olib chiqish jarayoni...");
        log.info("Field: {}, Query: {}, pageNumber: {}, pageSize: {}", field, query, pageNumber, pageSize);

        field = field == null ? "default" : field;
        Sort.Direction direction = sortDirection.equalsIgnoreCase("asc") ? Sort.Direction.ASC : Sort.Direction.DESC;
        Pageable pageable = PageRequest.of(--pageNumber, pageSize, Sort.by(direction, "id"));

        Page<History> page = switch (field) {
            case "userID" -> historyRepository.findByUserId(Long.parseLong(query), pageable);
            case "cardNumber" -> historyRepository.findByUserCardNumber(query, pageable);
            case "inventoryNumber" -> historyRepository.findByBookInventoryNumber(query, pageable);
            case "default" -> historyRepository.findAll(pageable);
            default -> throw new IllegalArgumentException("Noto'g'ri qidiruv maydoni: " + field);
        };

        List<History> content = page.getContent();

        log.info("History ro'yxati: Ro'yxat: {}, Sahifa raqami: {}, Sahifa hajmi: {}",
                content.stream().map(History::getId).toList(), pageNumber + 1, pageSize);

        Map<String, Object> map = Util.getPageInfo(page);
        map.put("data", content.stream().map(HistoryShortDTO::toDTO).toList());

        log.info("Tarix ro'yxatini olib chiqish yakunlandi.");

        return ResponseEntity.ok(new ResponseMessage(true, "Qidiruv natijalari muvaffaqiyatli qaytarildi", map));
    }

    @Override
    public List<History> getAll() {
        return historyRepository.findAll();
    }
}
