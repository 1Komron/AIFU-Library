package aifu.project.common_domain.mapper;

import aifu.project.common_domain.dto.live_dto.BookCopyCreateDTO;
import aifu.project.common_domain.dto.live_dto.BookCopyResponseDTO;
import aifu.project.common_domain.entity.BaseBook;
import aifu.project.common_domain.entity.BookCopy;

public class BookCopyMapper {

    public static BookCopy toEntity(BookCopyCreateDTO dto, BaseBook baseBook) {
        return BookCopy.builder()
                .inventoryNumber(dto.getInventoryNumber())
                .shelfLocation(dto.getShelfLocation())
                .notes(dto.getNotes())
                .book(baseBook)
                .build();
    }

    public static BookCopyResponseDTO toResponseDTO(BookCopy entity) {
        BookCopyResponseDTO dto = new BookCopyResponseDTO();
        dto.setId(entity.getId());
        dto.setInventoryNumber(entity.getInventoryNumber());
        dto.setShelfLocation(entity.getShelfLocation());
        dto.setNotes(entity.getNotes());
        dto.setBaseBookId(entity.getBook().getId());
        return dto;
    }

    private BookCopyMapper() {
    }

}
