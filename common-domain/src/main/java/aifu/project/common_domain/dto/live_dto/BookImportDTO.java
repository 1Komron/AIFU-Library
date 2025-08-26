package aifu.project.common_domain.dto.live_dto;

import aifu.project.common_domain.entity.BaseBook;
import aifu.project.common_domain.entity.BaseBookCategory;

import java.util.List;

public record BookImportDTO(
        String author,
        String title,
        String category,
        String series,
        Integer publicationYear,
        String publisher,
        String publicationCity,
        String isbn,
        Integer pageCount,
        String language,
        String udc,
        List<String> inventoryNumbers
) {
    public static BaseBook createBaseBook(BookImportDTO bookImportDTO, BaseBookCategory category) {
        BaseBook baseBook = new BaseBook();
        baseBook.setAuthor(bookImportDTO.author);
        baseBook.setTitle(bookImportDTO.title);
        baseBook.setCategory(category);
        baseBook.setSeries(bookImportDTO.series);
        baseBook.setPublicationYear(bookImportDTO.publicationYear);
        baseBook.setPublisher(bookImportDTO.publisher);
        baseBook.setPublicationCity(bookImportDTO.publicationCity);
        baseBook.setIsbn(bookImportDTO.isbn);
        baseBook.setPageCount(bookImportDTO.pageCount);
        baseBook.setLanguage(bookImportDTO.language);
        baseBook.setUdc(bookImportDTO.udc);
        baseBook.setDeleted(false);

        return baseBook;
    }
}
