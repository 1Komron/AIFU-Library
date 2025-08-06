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
    public ResponseEntity<ResponseMessage> create(CreateCategoryRequest request) {
        String name = request.name();

        BaseBookCategory category = categoryRepository.findByName(name);

        if (category != null && !category.isDeleted()) {
            log.error("'{}' -> nomli BaseBookCategory allaqachon mavjud (CREATE)", name);
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(new ResponseMessage(false, "BaseBookCategory allaqachon mavjud", null));
        }

        if (category != null) {
            category.setDeleted(false);
            category = categoryRepository.save(category);
            log.info("'{}' -> nomli BaseBookCategory o'chirilgan, qayta tiklandi", name);

            BaseBookCategoryDTO dto = BaseBookCategoryDTO.toDTO(category);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(new ResponseMessage(true, "BaseBookCategory qayta tiklandi", dto));
        }

        category = new BaseBookCategory();
        category.setName(name);
        category = categoryRepository.save(category);
        BaseBookCategoryDTO dto = BaseBookCategoryDTO.toDTO(category);

        log.info("BaseBookCategory yaratildi: {}", category);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ResponseMessage(true, "BaseBookCategory yaratildi", dto));
    }

    @Override
    public ResponseEntity<ResponseMessage> update(Integer id, UpdateCategoryRequest request) {
        BaseBookCategory category = categoryRepository.findByIdAndIsDeletedFalse(id)
                .orElseThrow(() -> new BaseBookCategoryNotFoundException(id));

        boolean exists = categoryRepository.existsByName(request.name());
        if (exists) {
            log.error("'{}' -> nomli BaseBookCategory allaqachon mavjud (UPDATE). Update qilinayotgan category: {}",
                    request.name(), category);
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(new ResponseMessage(false, "BaseBookCategory allaqachon mavjud", null));
        }

        category.setName(request.name());
        category = categoryRepository.save(category);
        BaseBookCategoryDTO dto = BaseBookCategoryDTO.toDTO(category);

        log.info("BaseBookCategory nomi tahrirlandi: {}", category);

        return ResponseEntity.ok(new ResponseMessage(true, "BaseBookCategory tahrirlandi", dto));
    }

    @Override
    public ResponseEntity<ResponseMessage> delete(Integer id) {
        BaseBookCategory category = categoryRepository.findByIdAndIsDeletedFalse(id)
                .orElseThrow(() -> new BaseBookCategoryNotFoundException(id));

        List<BaseBook> baseBookList = baseBookRepository.findByCategory_IdAndIsDeletedFalse(id);

        boolean allBooksDeleted = baseBookList.stream()
                .allMatch(BaseBook::isDeleted);

        if (!allBooksDeleted)
            throw new CategoryDeletionException("BaseBookCategory ni o'chirib bo'lmaydi. O'chirilmagan BaseBook mavjud. BaseBookCategory ID: " + id);

        category.setDeleted(true);
        categoryRepository.save(category);

        log.info("BaseBookCategory o'chirildi: {}", category);

        return ResponseEntity.ok(new ResponseMessage(true, "BaseBookCategory o'chirildi", id));
    }

    @Override
    public ResponseEntity<ResponseMessage> getList(String sortDirection) {
        Sort.Direction direction = sortDirection.equalsIgnoreCase("asc") ? Sort.Direction.ASC : Sort.Direction.DESC;
        List<BaseBookCategoryShortDTO> list = categoryRepository.findAllByIsDeletedFalse(Sort.by(direction, "id"));

        log.info("BaseBookCategory ro'yxati olindi. Ro'yxat: {}.  Elementlar soni: {}", list, list.size());

        return ResponseEntity.ok(new ResponseMessage(true, "BaseBookCategory lar ro'yxati", list));
    }

    @Override
    public ResponseEntity<ResponseMessage> get(Integer id) {
        BaseBookCategory category = categoryRepository.findByIdAndIsDeletedFalse(id)
                .orElseThrow(() -> new BaseBookCategoryNotFoundException(id));

        log.info("BaseBookCategory topildi: {}", category);

        BaseBookCategoryDTO dto = BaseBookCategoryDTO.toDTO(category);
        return ResponseEntity.ok(new ResponseMessage(true, "BaseBookCategory", dto));
    }
}