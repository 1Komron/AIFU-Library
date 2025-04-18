package aifu.project.libraryweb.service;

import aifu.project.commondomain.entity.BaseBook;
import aifu.project.commondomain.entity.BookCopy;
import aifu.project.commondomain.repository.BaseBookRepository;
import aifu.project.commondomain.repository.BookCopyRepository;
import aifu.project.libraryweb.dto.BookCopyDTO;


import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

import static aifu.project.libraryweb.mapper.BaseBookMapper.fromEntity;
import static aifu.project.libraryweb.mapper.BookCopyMapper.fromDTO;
import static aifu.project.libraryweb.mapper.BookCopyMapper.toDTO;

@Service
@RequiredArgsConstructor
public class BaseBookService {

    private final BaseBookRepository baseBookRepository;
    private final BookCopyRepository copyRepository;

    public BookCopyDTO create(BookCopyDTO dto) {
        if (dto == null || dto.getBaseBook() == null) {
            throw new IllegalArgumentException("BookCopy or BaseBook cannot be null");
        }

        // DTO -> Entity
        BookCopy bookCopy = fromDTO(dto);
        BaseBook savedBaseBook = baseBookRepository.save(bookCopy.getBook()); // bazaga yoziladi
        bookCopy.setBook(savedBaseBook); // BookCopyga BaseBookni qo‘yamiz

        BookCopy savedCopy = copyRepository.save(bookCopy); // saqlaymiz
        return toDTO(savedCopy); // qaytariladi
    }

    public List<BookCopy> getAll() {
        return copyRepository.findAll();

    }

    public BookCopyDTO getById(Integer id) {
        BookCopy bookCopy = copyRepository.findById(Long.valueOf(id))
                .orElseThrow(() -> new RuntimeException("BookCopy not found with id: " + id));

        return toDTO(bookCopy);
    }

    public BookCopyDTO update(Integer id, BookCopyDTO dto) {
        BookCopy existing = copyRepository.findById(Long.valueOf(id))
                .orElseThrow(() -> new RuntimeException("BookCopy not found"));

        existing.setInventoryNumber(dto.getInventoryNumber());
        existing.setNotes(dto.getNotes());
        existing.setShelfLocation(dto.getShelfLocation());

        BaseBook baseBook = fromEntity(dto.getBaseBook());
        baseBook.setId(existing.getBook().getId());

        BaseBook updateBook=baseBookRepository.save(baseBook);
        existing.setBook(updateBook);

        BookCopy updated = copyRepository.save(existing);
        return toDTO(updated);
    }

    public void delete(Integer id) {
        BookCopy existing = copyRepository.findById(Long.valueOf(id))
                .orElseThrow(() -> new RuntimeException("BookCopy not found"));
        BaseBook baseBook=existing.getBook();
        copyRepository.delete(existing);
        baseBookRepository.delete(baseBook);
    }


}

