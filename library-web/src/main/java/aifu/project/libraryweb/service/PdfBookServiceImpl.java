package aifu.project.libraryweb.service;

import aifu.project.commondomain.entity.Category;
import aifu.project.commondomain.entity.PdfBook;
import aifu.project.commondomain.repository.PdfBookRepository;
import aifu.project.libraryweb.dto.PdfBookDTO;
import aifu.project.libraryweb.mapper.PdfBookMapper;
import lombok.RequiredArgsConstructor;
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
    private final String fileStorageLocation = "E:/files/pdf";

    @Override
    public PdfBookDTO create(Integer categoryId, PdfBookDTO dto) {
        Category category = categoryService.getEntityById(categoryId);

        PdfBook book = PdfBook.builder()
                .author(dto.getAuthor())
                .title(dto.getTitle())
                .publicationYear(dto.getPublicationYear())
                .localDate(dto.getLocalDate())
                .pdfUrl(dto.getPdfUrl())
                .imageUrl(dto.getImageUrl())
                .category(category)
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
        // you could also allow changing URLs here if needed
        PdfBook updated = pdfBookRepository.save(book);
        return PdfBookMapper.toDto(updated);
    }

    @Override
    public void delete(Integer id) {
        pdfBookRepository.deleteById(id);
    }

    private String getPdfFileNameById(Integer id) {
        return "book_" + id + ".pdf";

    }

    public byte[] downloadPdf(Integer id) {
        PdfBook pdfBook = pdfBookRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("PDF Book not found with id: " + id));

        String fileName = pdfBook.getPdfUrl(); // pdfUrl - bazada saqlangan fayl nomi
        Path filePath = Paths.get(fileStorageLocation, fileName);
        try {
            return Files.readAllBytes(filePath);
        } catch (IOException e) {
            throw new RuntimeException("Faylni o'qishda xatolik yuz berdi: " + fileName, e);
        }

    }
}
