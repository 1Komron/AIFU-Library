package aifu.project.libraryweb.service.pdf_book_Service;

import aifu.project.commondomain.dto.pdf_book_dto.PdfBookCreateDTO;
import aifu.project.commondomain.dto.pdf_book_dto.PdfBookResponseDTO;
import aifu.project.commondomain.dto.pdf_book_dto.PdfBookUpdateDTO;
import aifu.project.commondomain.entity.Category;
import aifu.project.commondomain.entity.PdfBook;
import aifu.project.commondomain.mapper.PdfBookMapper;
import aifu.project.libraryweb.repository.PdfBookRepository;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PdfBookServiceImpl implements PdfBookService {

    private final PdfBookRepository pdfBookRepository;
    private final CategoryService categoryService;
    private final FileStorageService fileStorageService;

    @Value("${file.upload-dir:E:/files}")
    private String fileStorageLocation;

    @Override
    public PdfBookResponseDTO create(Integer categoryId, PdfBookCreateDTO dto) {

        Category category = categoryService.getEntityById(categoryId);
        // 2. DTO dan Entity ga mapping (Mapper ishlatish)
        PdfBook book = PdfBookMapper.toEntity(dto);

         book.setCategory(category);
        // 4. Saqlash
        PdfBook saved = pdfBookRepository.save(book);

        // 5. Response DTO ga mapping
        return PdfBookMapper.toDto(saved);
    }

    @Override
    public List<PdfBookResponseDTO> getAll() {
        List<PdfBook> books = pdfBookRepository.findAll();
        return books.stream()
                .map(PdfBookMapper::toDto)
                .toList();
    }

    @Override
    public PdfBookResponseDTO getOne(Integer id) {
        PdfBook book = pdfBookRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("PdfBook not found with id: " + id));

        return PdfBookMapper.toDto(book);
    }

    @Override
    public PdfBookResponseDTO update(Integer id, PdfBookUpdateDTO dto) {
        // 1. Mavjud entity ni topish
        PdfBook book = pdfBookRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("PdfBook not found with id: " + id));

        // 2. Category ni tekshirish (agar o'zgartirilayotgan bo'lsa)
        if (dto.getCategoryId() != null) {
            Category category = categoryService.getEntityById(dto.getCategoryId());
            book.setCategory(category);
        }

        // 3. Mapper orqali yangilash (null qiymatlar update qilinmaydi)
        PdfBookMapper.updateEntity(dto, book);

        // 4. Saqlash
        PdfBook updated = pdfBookRepository.save(book);

        // 5. Response qaytarish
        return PdfBookMapper.toDto(updated);
    }

    @Override
    public void delete(Integer id) {
        // Avval mavjudligini tekshirish
        if (!pdfBookRepository.existsById(id)) {
            throw new RuntimeException("PdfBook not found with id: " + id);
        }

        pdfBookRepository.deleteById(id);
    }

    @Override
    public byte[] downloadPdf(Integer id) {
        // 1. PdfBook ni topish
        PdfBook pdfBook = pdfBookRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("PDF Book not found with id: " + id));

        // 2. PDF URL ni tekshirish
        String filePath = pdfBook.getPdfUrl();
        if (filePath == null || filePath.trim().isEmpty()) {
            throw new RuntimeException("PDF fayl URL topilmadi");
        }

        // 3. Fayl yo'lini aniqlash
        String fileName = filePath.substring(filePath.lastIndexOf("/") + 1);
        Path path = Paths.get(fileStorageLocation, "pdf", fileName);

        // 4. Faylni o'qish
        try {
            if (!Files.exists(path)) {
                throw new RuntimeException("Fayl topilmadi: " + path.toString());
            }

            return Files.readAllBytes(path);

        } catch (IOException e) {
            throw new RuntimeException("Faylni o'qishda xatolik: " + e.getMessage(), e);
        }
    }
}