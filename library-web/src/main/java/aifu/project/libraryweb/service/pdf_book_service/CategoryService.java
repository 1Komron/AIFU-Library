package aifu.project.libraryweb.service.pdf_book_service;


import aifu.project.common_domain.dto.pdf_book_dto.*;
import aifu.project.common_domain.entity.Category;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface CategoryService {

    // Category yaratish
    CategoryResponseDTO create(CreateCategoryDTO createCategoryDTO);

    // Category yangilash
    CategoryResponseDTO update(Integer id, UpdateCategoryDTO updateCategoryDTO);

    // Category o'chirish
    void delete(Integer id);

    // ID bo'yicha Category olish
    CategoryResponseDTO getById(Integer id);

    // Barcha Categorylarni olish
    List<CategoryResponseDTO> getAll();

    // Entity sifatida Category olish (internal use)
    Category getEntityById(Integer categoryId);

    // Category bo'yicha kitoblarni olish
    List<PdfBookPreviewDTO> getBooksByCategoryId(Integer categoryId);

}