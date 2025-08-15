
package aifu.project.libraryweb.service.pdf_book_service;

import aifu.project.common_domain.dto.pdf_book_dto.*;
import aifu.project.common_domain.entity.Category;
import aifu.project.common_domain.entity.PdfBook;
import aifu.project.common_domain.exceptions.CategoryNotFoundException;
import aifu.project.common_domain.exceptions.PdfBookNotFoundException;
import aifu.project.common_domain.exceptions.PdfFileDownloadException;
import aifu.project.common_domain.mapper.PdfBookMapper;
import aifu.project.libraryweb.lucene.LuceneIndexService;
import aifu.project.libraryweb.repository.PdfBookRepository;
import aifu.project.libraryweb.utils.Util;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.List;
import java.util.Map;


@Slf4j
@Service
@RequiredArgsConstructor
public class PdfBookServiceImpl implements PdfBookService {

    private final PdfBookRepository pdfBookRepository;
    private final CategoryService categoryService;
    private final LuceneIndexService luceneIndexService;


    /**
     * Yangi PDF kitob yaratadi, uni ma'lumotlar bazasiga saqlaydi va qidiruv uchun indekslaydi.
     *
     * @param dto Yangi kitobning ma'lumotlarini o'z ichiga olgan DTO (Data Transfer Object).
     * @return Yaratilgan kitobning ma'lumotlarini o'z ichiga olgan {@link PdfBookResponseDTO}.
     */
    @Override
    public PdfBookResponseDTO create(PdfBookCreateDTO dto) {
        log.info("Attempting to create a new PDF book for category ID: {}", dto.getCategoryId());
        Category category = categoryService.getById(dto.getCategoryId());

        PdfBook book = PdfBookMapper.toEntity(dto);
        book.setCategory(category);

        PdfBook saved = pdfBookRepository.save(book);
        log.info("PDF book saved successfully with ID: {}", saved.getId());

        try {
            luceneIndexService.indexPdfBooks(saved);
            log.info("Lucene index created for PdfBook ID: {}", saved.getId());
        } catch (IOException e) {
            log.error("PDF book indexing failed for ID: {}. Error: {}", saved.getId(), e.getMessage());
        }

        return PdfBookMapper.toDto(saved);
    }


    @Override
    public Map<String, Object> getList(int pageNumber, int pageSize, Integer category) {
        log.info("Retrieving paginated list of PDF books. Page: {}, Size: {}", pageNumber, pageSize);
        Pageable pageable = PageRequest.of(pageNumber - 1, pageSize, Sort.by(Sort.Direction.ASC, "id"));

        Page<PdfBook> page = (category == null)
                ? pdfBookRepository.findAll(pageable)
                : pdfBookRepository.findAllByCategory_Id(category, pageable);

        Map<String, Object> map = Util.getPageInfo(page);

        List<PdfBookPreviewDTO> list = page.getContent().stream()
                .map(PdfBookMapper::toPreviewDto)
                .toList();

        map.put("data", list);
        return map;
    }


    @Override
    public PdfBookResponseDTO getOne(Integer id) {
        log.info("Attempting to retrieve PDF book with ID: {}", id);
        PdfBook book = pdfBookRepository.findById(id)
                .orElseThrow(() -> new PdfBookNotFoundException("PDF Book not found with id: " + id));
        log.info("Successfully retrieved PDF book with ID: {}", id);
        return PdfBookMapper.toDto(book);
    }


    @Override
    public PdfBookResponseDTO update(Integer id, Map<String, Object> updates) {
        log.info("Attempting to PATCH update PDF book with ID: {}", id);

        PdfBook entity = pdfBookRepository.findById(id)
                .orElseThrow(() -> new PdfBookNotFoundException("PDF book topilmadi. ID: " + id));

        for (Map.Entry<String, Object> entry : updates.entrySet()) {
            String key = entry.getKey();
            Object value = entry.getValue();

            if (key == null)
                throw new IllegalArgumentException("Key null");

            if (value == null)
                throw new IllegalArgumentException("Value null");

            switch (key) {
                case "title" -> {
                    if (value instanceof String title && !title.isBlank()) {
                        entity.setTitle(title);
                    }
                }
                case "author" -> {
                    if (value instanceof String author && !author.isBlank()) {
                        entity.setAuthor(author);
                    }
                }
                case "language" -> {
                    if (value instanceof String language) {
                        entity.setLanguage(language);
                    }
                }
                case "pageCount" -> {
                    if (value instanceof Integer pageCount) {
                        entity.setPageCount(pageCount);
                    }
                }
                case "publisher" -> {
                    if (value instanceof String publisher) {
                        entity.setPublisher(publisher);
                    }
                }
                case "description" -> {
                    if (value instanceof String description) {
                        entity.setDescription(description);
                    }
                }
                case "pdfUrl" -> {
                    if (value instanceof String pdfUrl && !pdfUrl.isBlank()) {
                        entity.setPdfUrl(pdfUrl);
                    }
                }
                case "categoryId" -> {
                    if (value instanceof Integer categoryId) {
                        Category category = categoryService.getById(categoryId);
                        entity.setCategory(category);
                    } else {
                        throw new IllegalArgumentException("Category ID must be an Integer.");
                    }
                }
                default -> throw new IllegalArgumentException("Invalid field for update: " + key);
            }
        }

        PdfBook updatedBook = pdfBookRepository.save(entity);
        log.info("PDF book tahrirlandi: {}.\nTahrirlanga field lar: {}", entity, updates.keySet());

        try {
            luceneIndexService.indexPdfBooks(updatedBook);
            log.info("Lucene index updated for PdfBook ID: {}", updatedBook.getId());
        } catch (IOException e) {
            log.error("Failed to update Lucene index for PdfBook ID: {}. Error: {}", updatedBook.getId(), e.getMessage());
        }

        return PdfBookMapper.toDto(updatedBook);
    }


    @Override
    public void delete(Integer id) {
        log.info("Attempting to delete PDF book with ID: {}", id);
        if (!pdfBookRepository.existsById(id)) {
            throw new PdfBookNotFoundException("Cannot delete. PDF Book not found with id: " + id);
        }
        pdfBookRepository.deleteById(id);
        log.info("Successfully deleted PDF book with ID: {}", id);

        try {
            luceneIndexService.deletePDFBookIndex(id);
            log.info("Lucene index deleted for PdfBook ID: {}", id);
        } catch (IOException e) {
            log.error("Failed to delete Lucene index for PdfBook ID: {}. Error: {}", id, e.getMessage());
        }
    }


    @Override
    public byte[] downloadPdf(Integer id) {
        log.info("Attempting to download PDF file for book ID: {}", id);
        PdfBook pdfBook = pdfBookRepository.findById(id)
                .orElseThrow(() -> new PdfBookNotFoundException("PDF Book not found with id: " + id));

        String fileUrl = pdfBook.getPdfUrl();
        if (fileUrl == null || fileUrl.trim().isEmpty()) {
            throw new PdfBookNotFoundException("PDF file URL not found for book with id: " + id);
        }

        log.info("Downloading file from URL: {}", fileUrl);
        try (InputStream in = new URL(fileUrl).openStream()) {
            byte[] fileBytes = in.readAllBytes();
            log.info("Successfully downloaded {} bytes for book ID: {}", fileBytes.length, id);
            return fileBytes;
        } catch (IOException e) {
            throw new PdfFileDownloadException("Error reading file from URL: " + fileUrl, e);
        }
    }


    @Override
    public Map<String, Object> getAll(PdfBookSearchCriteriaDTO criteria) {
        log.info("Searching for PDF books with criteria: {}", criteria);
        String field = criteria.getField() == null ? "default" : criteria.getField();
        String query = criteria.getQuery();

        if (!"default".equals(field) && (query == null || query.isBlank())) {
            throw new IllegalArgumentException("Query value cannot be null or empty for field: " + field);
        }

        Sort.Direction direction = "desc".equalsIgnoreCase(criteria.getSortDr()) ? Sort.Direction.DESC : Sort.Direction.ASC;
        Pageable pageable = PageRequest.of(criteria.getPageNumber() - 1, criteria.getSize(), Sort.by(direction, "id"));

        Page<PdfBook> resultPage = switch (field) {
            case "fullInfo" -> {
                String[] parts = query.trim().split("\\s+");
                String first = "%" + parts[0].toLowerCase() + "%";
                String second = (parts.length > 1) ? "%" + parts[1].toLowerCase() + "%" : null;
                yield pdfBookRepository.findByAuthorAndTitle(first, second, pageable);
            }
            case "categoryId" -> {
                try {
                    yield pdfBookRepository.findByCategoryId(Integer.parseInt(query), pageable);
                } catch (NumberFormatException e) {
                    throw new IllegalArgumentException("Category ID must be a number. Invalid value: " + query);
                }
            }
            case "default" -> pdfBookRepository.findAll(pageable);
            default -> throw new IllegalArgumentException("Invalid search field provided: " + field);
        };

        List<PdfBook> content = resultPage.getContent();
        log.info("PDF kitoblar ro'yxati olindi. Ro'yxat: {}", content.stream().map(PdfBook::getId).toList());

        List<PdfBookResponseDTO> bookList = content.stream()
                .map(PdfBookMapper::toDto)
                .toList();

        Map<String, Object> map = Util.getPageInfo(resultPage);
        map.put("data", bookList);

        return map;
    }
}