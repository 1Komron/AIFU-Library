package aifu.project.libraryweb.service;

import aifu.project.commondomain.dto.PdfBookDTO;
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
    public PdfBookDTO create(Integer categoryId, PdfBookDTO dto) {
        Category category = categoryService.getEntityById(categoryId);
        PdfBook book = PdfBook.builder()
                .isbn(dto.getIsbn())
                .language(dto.getLanguage())
                .publisher(dto.getPublisher())
                .script(dto.getScript())
                .author(dto.getAuthor())
                .title(dto.getTitle())
                .publicationYear(dto.getPublicationYear())
                .localDate(dto.getLocalDate())
                .pdfUrl(dto.getPdfUrl())
                .imageUrl(dto.getImageUrl())
                //.category(category)
                .pageCount(dto.getPageCount())
                .build();
        PdfBook saved = pdfBookRepository.save(book);
        return PdfBookMapper.toDto(saved);
    }

    @Override
    public List<PdfBookDTO> getAllByCategory(Integer categoryId) {
        Category category = categoryService.getEntityById(categoryId);
        return category.getBooks().stream()
                .map(PdfBookMapper::toDto)
                .toList();
    }

    @Override
    public PdfBookDTO getOne(Integer id) {
        PdfBook book = pdfBookRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("PdfBook not found"));
        return PdfBookMapper.toDto(book);
    }

    @Override
    public PdfBookDTO update(Integer id, PdfBookDTO dto) {
        PdfBook book = pdfBookRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("PdfBook not found"));
        book.setAuthor(dto.getAuthor());
        book.setTitle(dto.getTitle());
        book.setPublicationYear(dto.getPublicationYear());
        book.setLocalDate(dto.getLocalDate());
        book.setPdfUrl(dto.getPdfUrl());
        book.setImageUrl(dto.getImageUrl());
        book.setScript(dto.getScript());
        book.setLanguage(dto.getLanguage());
        book.setPageCount(dto.getPageCount());
        book.setPublisher(dto.getPublisher());
        book.setIsbn(dto.getIsbn());
        PdfBook updated = pdfBookRepository.save(book);
        return PdfBookMapper.toDto(updated);
    }

    @Override
    public void delete(Integer id) {
        pdfBookRepository.deleteById(id);
    }

    @Override
    public byte[] downloadPdf(Integer id) {
        PdfBook pdfBook = pdfBookRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("PDF Book not found with id: " + id));
        String filePath = pdfBook.getPdfUrl();
        if (filePath == null || filePath.isEmpty()) {
            throw new RuntimeException("PDF fayl URL topilmadi");
        }
        String fileName = filePath.substring(filePath.lastIndexOf("/") + 1);
        Path path = Paths.get(fileStorageLocation, "pdf", fileName);
        try {
            if (!Files.exists(path)) {
                throw new RuntimeException("Fayl topilmadi: " + path);
            }
            return Files.readAllBytes(path);
        } catch (IOException e) {
            throw new RuntimeException("Faylni o'qishda xatolik: " + e.getMessage());
        }
    }

}