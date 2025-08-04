package aifu.project.common_domain.dto.histroy_dto;

import aifu.project.common_domain.entity.BaseBook;
import aifu.project.common_domain.entity.BookCopy;

public record HistoryBookDTO(
        Integer id,
        String author,
        String title,
        String inventoryNumber,
        String isbn,
        String udc
) {
    public static HistoryBookDTO toDTO(BookCopy copy) {
        BaseBook book = copy.getBook();
        return new HistoryBookDTO(
                copy.getId(),
                book.getAuthor(),
                book.getTitle(),
                copy.getInventoryNumber(),
                book.getIsbn(),
                book.getUdc()
        );
    }
}
