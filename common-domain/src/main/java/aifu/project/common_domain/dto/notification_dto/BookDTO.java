package aifu.project.common_domain.dto.notification_dto;


import aifu.project.common_domain.entity.BaseBook;
import aifu.project.common_domain.entity.BookCopy;

public record BookDTO(
        String title,
        String author,
        String isbn,
        String inventoryNumber,
        String epc
) {
    public static BookDTO toDTO(BookCopy bookCopy) {
        BaseBook baseBook = bookCopy.getBook();
        return new BookDTO(
                baseBook.getTitle(),
                baseBook.getAuthor(),
                baseBook.getIsbn(),
                bookCopy.getInventoryNumber(),
                bookCopy.getEpc()
        );
    }
}
