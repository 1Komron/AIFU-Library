package aifu.project.common_domain.dto.histroy_dto;

import aifu.project.common_domain.entity.BaseBook;
import aifu.project.common_domain.entity.BookCopy;
import aifu.project.common_domain.entity.History;
import aifu.project.common_domain.entity.Student;

import java.time.LocalDate;

public record HistoryShortDTO(
        Long id,
        String name,
        String surname,
        String author,
        String bookTitle,
        String inventoryNumber,
        LocalDate givenAt,
        LocalDate dueDate,
        LocalDate returnedAt
) {
    public static HistoryShortDTO toDTO(History history) {
        Student user = history.getUser();
        BookCopy bookCopy = history.getBook();
        BaseBook baseBook = bookCopy.getBook();

        return new HistoryShortDTO(
                history.getId(),
                user.getName(),
                user.getSurname(),
                baseBook.getAuthor(),
                baseBook.getTitle(),
                bookCopy.getInventoryNumber(),
                history.getGivenAt(),
                history.getDueDate(),
                history.getReturnedAt()
        );
    }
}
