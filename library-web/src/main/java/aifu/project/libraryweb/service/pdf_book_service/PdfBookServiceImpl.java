package aifu.project.libraryweb.service.pdf_book_service;

import aifu.project.common_domain.dto.ResponseMessage;
import aifu.project.common_domain.dto.pdf_book_dto.*;
import aifu.project.common_domain.entity.Category;
import aifu.project.common_domain.entity.PdfBook;
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
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.List;
import java.util.Map;

import static aifu.project.libraryweb.utils.UpdateUtils.updateIfChanged;


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
    public PdfBookResponseDTO update(Integer id, PdfBookUpdateDTO updates) {
        log.info("Attempting to PATCH update PDF book with ID: {}", id);

        PdfBook entity = pdfBookRepository.findById(id)
                .orElseThrow(() -> new PdfBookNotFoundException("PDF book topilmadi. ID: " + id));

        updateFields(entity, updates);

        PdfBook updatedBook = pdfBookRepository.save(entity);
        log.info("PDF book tahrirlandi: {}.\nTahrirlanga field lar: {}", entity, updates);

        try {
            luceneIndexService.indexPdfBooks(updatedBook);
            log.info("Lucene index updated for PdfBook ID: {}", updatedBook.getId());
        } catch (IOException e) {
            log.error("Failed to update Lucene index for PdfBook ID: {}. Error: {}", updatedBook.getId(), e.getMessage());
        }

        return PdfBookMapper.toDto(updatedBook);
    }

    private void updateFields(PdfBook entity, PdfBookUpdateDTO updates) {
        updateIfChanged(updates.getAuthor(), entity::getAuthor, entity::setAuthor);
        updateIfChanged(updates.getTitle(), entity::getTitle, entity::setTitle);
        updateIfChanged(updates.getPublicationYear(), entity::getPublicationYear, entity::setPublicationYear);
        updateIfChanged(updates.getPdfUrl(), entity::getPdfUrl, entity::setPdfUrl);
        updateIfChanged(updates.getImageUrl(), entity::getImageUrl, entity::setImageUrl);
        updateIfChanged(updates.getIsbn(), entity::getIsbn, entity::setIsbn);
        updateIfChanged(updates.getPageCount(), entity::getPageCount, entity::setPageCount);
        updateIfChanged(updates.getPublisher(), entity::getPublisher, entity::setPublisher);
        updateIfChanged(updates.getLanguage(), entity::getLanguage, entity::setLanguage);
        updateIfChanged(updates.getScript(), entity::getScript, entity::setScript);
        updateIfChanged(updates.getSize(), entity::getSize, entity::setSize);
        updateIfChanged(updates.getDescription(), entity::getDescription, entity::setDescription);

        if (updates.getCategoryId() != null && !updates.getCategoryId().equals(entity.getCategory().getId())) {
            Category newCategory = categoryService.getById(updates.getCategoryId());
            entity.setCategory(newCategory);
        }
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
                String[] parts = query.trim().split("~");
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

        List<PdfBookShortDTO> bookList = content.stream()
                .map(PdfBookMapper::toPdfBookShortDTO)
                .toList();

        Map<String, Object> map = Util.getPageInfo(resultPage);
        map.put("data", bookList);

        return map;
    }

    @Override
    public ResponseEntity<ResponseMessage> getNewBooks() {
        List<PdfBookShortDTO> newBooks = pdfBookRepository.findNewBooks(PageRequest.of(0, 6));

        log.info("Yangi pdf kitoblar ro'yxati: {}", newBooks.stream().map(PdfBookShortDTO::getId).toList());

        return ResponseEntity.ok(new ResponseMessage(true, "Yangi pdf kitoblar ro'yxati", newBooks));
    }

    @Override
    public ResponseEntity<ResponseMessage> showByCategories() {
        List<Category> categories = categoryService.getHomePageCategories();

        List<CategoryWithBooksDTO> list = categories.stream()
                .map(cat -> {
                            var obj = new CategoryWithBooksDTO(
                                    new CategoryPreviewDTO(cat.getId(), cat.getName()),
                                    pdfBookRepository.findTopBooksByCategory(cat.getId(), PageRequest.of(0, 6)));
                            log.info("Categroy boyicha kitoblar royxati. Category: {}, Ro'yxat: {}",
                                    obj.category().getId(), obj.books().stream().map(PdfBookShortDTO::getId).toList());

                            return obj;
                        }
                ).toList();


        return ResponseEntity.ok(new ResponseMessage(true, "Category boyicha kitob ro'yxati", list));
    }

    @Override
    public List<PdfBookShortDTO> getBooks(List<Long> list) {
        return pdfBookRepository.findAllByIdIn(list);
    }
}