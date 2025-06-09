package aifu.project.common_domain.mapper;

import aifu.project.common_domain.dto.live_dto.BookCopyDTO;
import aifu.project.common_domain.entity.BookCopy;
import aifu.project.common_domain.entity.BaseBook;

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