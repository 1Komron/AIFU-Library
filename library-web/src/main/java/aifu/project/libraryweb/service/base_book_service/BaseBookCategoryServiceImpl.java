package aifu.project.libraryweb.service.base_book_service;

import aifu.project.common_domain.dto.live_dto.BaseBookCategoryDTO;
import aifu.project.common_domain.dto.CreateCategoryRequest;
import aifu.project.common_domain.dto.UpdateCategoryRequest;
import aifu.project.common_domain.dto.live_dto.BaseBookCategoryShortDTO;
import aifu.project.common_domain.entity.BaseBook;
import aifu.project.common_domain.entity.BaseBookCategory;
import aifu.project.common_domain.exceptions.BaseBookCategoryNotFoundException;
import aifu.project.common_domain.exceptions.CategoryDeletionException;
import aifu.project.common_domain.dto.ResponseMessage;
import aifu.project.libraryweb.repository.BaseBookCategoryRepository;
import aifu.project.libraryweb.repository.BaseBookRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class BaseBookCategoryServiceImpl implements BaseBookCategoryService {
    private final BaseBookCategoryRepository categoryRepository;
    private final BaseBookRepository baseBookRepository;

    @Override
    @Transactional
    public ResponseEntity<ResponseMessage> create(CreateCategoryRequest request) {
        log.info("BaseBookCategory yaratilishi jarayoni...");
        log.info("Yangi BaseBookCategory yaratish so'rovi: {}", request);

        String name = request.name().trim();

        boolean exists = categoryRepository.existsByNameAndIsDeletedFalse(name);

        if (exists) {
            log.error("'{}' -> nomli BaseBookCategory allaqachon mavjud (CREATE)", name);
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(new ResponseMessage(false, "'%s' nomli kategoriya allaqachon mavjud".formatted(request.name()), null));
        }

        BaseBookCategory category = new BaseBookCategory();

        category.setName(name);
        category = categoryRepository.save(category);
        BaseBookCategoryDTO dto = BaseBookCategoryDTO.toDTO(category);

        log.info("BaseBookCategory yaratildi: {}", category);
        log.info("BaseBookCategory yaratish jarayoni yakunlandi.");

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ResponseMessage(true, "'%s' nomli kategoriya muvaffaqiyatli yaratildi", dto));
    }

    @Override
    @Transactional
    public ResponseEntity<ResponseMessage> update(Integer id, UpdateCategoryRequest request) {
        log.info("BaseBookCategory tahrirlanishi jarayoni...");
        log.info("BaseBookCategory tahrirlash so'rovi: {}, ID: {}", request, id);

        BaseBookCategory category = categoryRepository.findByIdAndIsDeletedFalse(id)
                .orElseThrow(() -> new BaseBookCategoryNotFoundException(id));

        String newName = request.name().trim();
        boolean exists = categoryRepository.existsByNameAndIsDeletedFalse(newName);

        if (exists) {
            log.error("'{}' -> nomli BaseBookCategory allaqachon mavjud (UPDATE). Update qilinayotgan category: {}",
                    newName, category);
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(new ResponseMessage(false, "'%s' nomli kategoriya allaqachon mavjud".formatted(request.name()), null));
        }

        category.setName(newName);
        category = categoryRepository.save(category);
        BaseBookCategoryDTO dto = BaseBookCategoryDTO.toDTO(category);

        log.info("BaseBookCategory nomi tahrirlandi: {}", category);
        log.info("BaseBookCategory tahrirlash jarayoni yakunlandi.");

        return ResponseEntity.ok(new ResponseMessage(true, "Kategoriya muvaffaqiyatli tahrirlandi", dto));
    }

    @Override
    @Transactional
    public ResponseEntity<ResponseMessage> delete(Integer id) {
        log.info("BaseBookCategory o'chirilishi jarayoni...");
        log.info("O'chirilishi so'ralgan BaseBookCategory ID: {}", id);

        BaseBookCategory category = categoryRepository.findByIdAndIsDeletedFalse(id)
                .orElseThrow(() -> new BaseBookCategoryNotFoundException(id));

        List<BaseBook> baseBookList = baseBookRepository.findByCategory_IdAndIsDeletedFalse(id);

        boolean allBooksDeleted = baseBookList.stream()
                .allMatch(BaseBook::isDeleted);

        if (!allBooksDeleted)
            throw new CategoryDeletionException("'%s' nomli kategoriyani o'chirib bo'lmaydi. O'chirilmagan kitoblar mavjud.");

        category.setDeleted(true);
        categoryRepository.save(category);

        log.info("BaseBookCategory o'chirildi: {}", category);
        log.info("BaseBookCategory o'chirish jarayoni yakunlandi.");

        return ResponseEntity
                .ok(new ResponseMessage(true,
                        "'%s' nomli kategoriya muvaffaqiyatli o'chirildi".formatted(category.getName()),
                        id));
    }

    @Override
    public ResponseEntity<ResponseMessage> getList(String sortDirection) {
        log.info("BaseBookCategory ro'yxatini olish jarayoni...");
        Sort.Direction direction = sortDirection.equalsIgnoreCase("desc") ? Sort.Direction.DESC : Sort.Direction.ASC;
        List<BaseBookCategoryShortDTO> list = categoryRepository.findAllByIsDeletedFalse(Sort.by(direction, "id"));

        log.info("BaseBookCategory ro'yxati olindi. Ro'yxat: {}.  Elementlar soni: {}", list, list.size());
        log.info("BaseBookCategory ro'yxatini olish jarayoni yakunlandi.");

        return ResponseEntity.ok(new ResponseMessage(true, "Kategoriyalar ro'yxati", list));
    }

    @Override
    public ResponseEntity<ResponseMessage> get(Integer id) {
        log.info("BaseBookCategory ID bo'yicha olish jarayoni...");
        BaseBookCategory category = categoryRepository.findByIdAndIsDeletedFalse(id)
                .orElseThrow(() -> new BaseBookCategoryNotFoundException(id));

        log.info("BaseBookCategory topildi: {}", category);
        log.info("BaseBookCategory ID bo'yicha olish jarayoni yakunlandi.");

        BaseBookCategoryDTO dto = BaseBookCategoryDTO.toDTO(category);
        return ResponseEntity.ok(new ResponseMessage(true, "Kategoryia ma'lumotlari", dto));
    }
}