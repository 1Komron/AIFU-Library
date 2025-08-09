
package aifu.project.libraryweb.service.pdf_book_service;

import aifu.project.common_domain.dto.pdf_book_dto.*;
import aifu.project.common_domain.entity.Category;
import aifu.project.common_domain.entity.PdfBook;
import aifu.project.common_domain.mapper.PdfBookMapper;
import aifu.project.libraryweb.lucene.LuceneIndexService;
import aifu.project.libraryweb.repository.PdfBookRepository;

import aifu.project.libraryweb.utils.Util;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class PdfBookServiceImpl implements PdfBookService {

    private final PdfBookRepository pdfBookRepository;
    private final CategoryService categoryService;
    private final FileStorageService fileStorageService;
    private final LuceneIndexService luceneIndexService;

    @Value("${file.upload-dir:E:/files}")
    private String fileStorageLocation;

    @Override
    public PdfBookResponseDTO create(Integer categoryId, PdfBookCreateDTO dto) {
        // 1. Categoryni olib kelamiz
        Category category = categoryService.getById(categoryId);

        // 2. DTO dan Entity ga mapping (Mapper ishlatish)
        PdfBook book = PdfBookMapper.toEntity(dto);
        book.setCategory(category);

        // 3. Saqlash
        PdfBook saved = pdfBookRepository.save(book);

        // 4. Lucene indekslash
        try {
            luceneIndexService.indexPdfBooks(saved);
        } catch (IOException e) {
            log.error("PDF book indexing failed. ID: {}, Error: {}", saved.getId(), e.getMessage());
        }

        // 5. Response DTO ga mapping
        return PdfBookMapper.toDto(saved);
    }


    @Override
    public Map<String, Object> getList(int pageNumber, int pageSize) {
        Pageable pageable = PageRequest.of(pageNumber - 1, pageSize,Sort.by(Sort.Direction.ASC,"id"));
        Page<PdfBook> page = pdfBookRepository.findAll(pageable);

        Map<String, Object> map = Util.getPageInfo(page);

        List<PdfBookPreviewDTO> list = page.getContent().stream()
                .map(PdfBookMapper::toPreviewDto)
                .toList();

        map.put("data", list);
        return map;
    }


    @Override
    public PdfBookResponseDTO getOne(Integer id) {
        PdfBook book = pdfBookRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("PdfBook not found with id: " + id));

        return PdfBookMapper.toDto(book);
    }

    @Override
    public PdfBookResponseDTO update(Integer id, PdfBookUpdateDTO dto) {
        // 1. Mavjud PdfBook entity ni topamiz
        PdfBook book = pdfBookRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("PdfBook not found with id: " + id));

        // 2. Category yangilanmoqchi bo‘lsa, olib kelamiz
        if (dto.getCategoryId() != null) {
            Category category = categoryService.getById(dto.getCategoryId());
            book.setCategory(category);
        }

        // 3. DTO ni mavjud Entity ga map qilamiz
        PdfBookMapper.updateEntity(dto, book);

        // 4. Entity ni saqlaymiz
        PdfBook updated = pdfBookRepository.save(book);

        // 5. Lucene indeksni yangilaymiz
        try {
            luceneIndexService.indexPdfBooks(updated);
            log.info("Lucene index updated for PdfBook ID: {}", updated.getId());
        } catch (IOException e) {
            log.error("Failed to update Lucene index for PdfBook ID: {}. Error: {}", updated.getId(), e.getMessage());
        }

        // 6. Response DTO qaytaramiz
        return PdfBookMapper.toDto(updated);
    }


    @Override
    public void delete(Integer id) {
        PdfBook book = pdfBookRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("PdfBook not found with id: " + id));
        pdfBookRepository.deleteById(id);

        try {
            luceneIndexService.deletePDFBookIndex(id);
            log.info("Lucene index deleted for PdfBook ID: {}", id);
        } catch (IOException e) {
            log.error("Failed to delete Lucene index for PdfBook ID: {}. Error: {}", id, e.getMessage());
        }
    }

    @Override
    public byte[] downloadPdf(Integer id) {
        PdfBook pdfBook = pdfBookRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("PDF Book not found with id: " + id));

        String fileUrl = pdfBook.getPdfUrl();
        if (fileUrl == null || fileUrl.trim().isEmpty()) {
            throw new RuntimeException("PDF fayl URL topilmadi");
        }

        // Cloudinary-dan faylni olish (HTTP GET orqali)
        try (InputStream in = new URL(fileUrl).openStream()) {
            return in.readAllBytes();
        } catch (IOException e) {
            throw new RuntimeException("Clouddan faylni o'qishda xatolik: " + e.getMessage(), e);
        }
    }


    @Override
    public List<PdfBookPreviewDTO> getBooksByCategoryId(Integer categoryId) {
        Category category = categoryService.getById(categoryId);
        return category.getBooks().stream()
                .map(PdfBookMapper::toPreviewDto)
                .collect(Collectors.toList());
    }

    @Override
    public Page<PdfBookResponseDTO> search(PdfBookSearchCriteriaDTO criteria) {
        Sort.Direction direction = criteria.getSortDr().equalsIgnoreCase("desc") ? Sort.Direction.DESC : Sort.Direction.ASC;
        Pageable pageable = PageRequest.of(criteria.getPageNumber() - 1, criteria.getSize(), Sort.by(direction, criteria.getSortBy()));

        Page<PdfBook> resultPage;
        if ("author".equalsIgnoreCase(criteria.getField())) {
            resultPage = pdfBookRepository.findByAuthorContainingIgnoreCase(criteria.getValue(), pageable);
        } else {
            resultPage = pdfBookRepository.findByTitleContainingIgnoreCase(criteria.getValue(), pageable);
        }

        return resultPage.map(PdfBookMapper::toDto);
    }


}