package aifu.project.libraryweb.mapper;

import aifu.project.commondomain.entity.BookCopy;
import aifu.project.libraryweb.dto.BookCopyDTO;
import org.springframework.stereotype.Component;

public class BookCopyMapper {

    //Entitydan dto ga uzgartirish
    public static BookCopyDTO toDTO(BookCopy bookCopy) {

        return BookCopyDTO.builder()
                .inventoryNumber(bookCopy.getInventoryNumber())
                .notes(bookCopy.getNotes())
                .shelfLocation(bookCopy.getShelfLocation())
                .baseBook(BaseBookMapper.toDTO(bookCopy.getBook()))
                .build();
    }

    //dto dan entityga utkazish
    public static BookCopy fromDTO(BookCopyDTO bookCopyDTO) {
        return BookCopy.builder()
                .notes(bookCopyDTO.getNotes())
                .inventoryNumber(bookCopyDTO.getInventoryNumber())
                .shelfLocation(bookCopyDTO.getShelfLocation())
                .book(BaseBookMapper.fromEntity(bookCopyDTO.getBaseBook()))
                .build();
    }
}
