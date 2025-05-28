package aifu.project.libraryweb.service.pdf_book_Service;


import aifu.project.commondomain.dto.pdf_book_dto.*;
import aifu.project.commondomain.entity.Category;
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
    List<PdfBookResponseDTO> getBooksByCategoryId(Integer categoryId);

}