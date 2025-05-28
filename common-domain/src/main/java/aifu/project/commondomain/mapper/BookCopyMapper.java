package aifu.project.commondomain.mapper;

import aifu.project.commondomain.dto.BookCopyDTO;
import aifu.project.commondomain.entity.BookCopy;
import aifu.project.commondomain.entity.BaseBook;

public class BookCopyMapper {
    public static BookCopyDTO toDto(BookCopy entity) {
        if (entity == null) return null;
        return BookCopyDTO.builder()
                .id(entity.getId())
                .inventoryNumber(entity.getInventoryNumber())
                .shelfLocation(entity.getShelfLocation())
                .notes(entity.getNotes())
                .baseBookId(entity.getBook() != null ? entity.getBook().getId() : null)
                .build();
    }

    public static BookCopy toEntity(BookCopyDTO dto, BaseBook baseBook) {
        if (dto == null) return null;
        return BookCopy.builder()
                .id(dto.getId())
                .inventoryNumber(dto.getInventoryNumber())
                .shelfLocation(dto.getShelfLocation())
                .notes(dto.getNotes())
                .book(baseBook)
                .build();
    }
}