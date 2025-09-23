package aifu.project.common_domain.dto.live_dto;

import aifu.project.common_domain.entity.BaseBook;
import aifu.project.common_domain.entity.BookCopy;

public record BookCopySummaryDTO(
        Integer baseBookId,
        String author,
        String title,
        String udc,
        String category,
        Integer bookCopyId,
        String inventoryNumber,
        String epc,
        String shelfLocation,
        String notes,
        Boolean isTaken
) {
    public static BookCopySummaryDTO toDTO(BookCopy bookCopy) {
        BaseBook baseBook = bookCopy.getBook();

        return new BookCopySummaryDTO(
                baseBook.getId(),
                baseBook.getAuthor(),
                baseBook.getTitle(),
                baseBook.getUdc(),
                baseBook.getCategory().getName(),
                bookCopy.getId(),
                bookCopy.getInventoryNumber(),
                bookCopy.getEpc(),
                bookCopy.getShelfLocation(),
                bookCopy.getNotes(),
                bookCopy.isTaken()
        );
    }
}
