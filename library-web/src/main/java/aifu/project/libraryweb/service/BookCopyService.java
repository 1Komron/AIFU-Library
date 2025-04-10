package aifu.project.libraryweb.service;

import aifu.project.libraryweb.dto.BookCopyDTO;
import aifu.project.commondomain.entity.BookCopy;
import aifu.project.commondomain.repository.BookCopyRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class BookCopyService {

    @Autowired
    private BookCopyRepository bookCopyRepository;

    public BookCopyDTO createBookCopy(BookCopyDTO bookCopyDTO) {
        BookCopy bookCopy = new BookCopy();
        bookCopy.setInventoryNumber(bookCopyDTO.getInventoryNumber());
        bookCopy.setShelfLocation(bookCopyDTO.getShelfLocation());
        bookCopy.setNotes(bookCopyDTO.getNotes());
        // Add book mapping if necessary
        BookCopy savedBookCopy = bookCopyRepository.save(bookCopy);
        return mapToDTO(savedBookCopy);
    }

    public BookCopyDTO getBookCopyById(Integer id) {
        BookCopy bookCopy = bookCopyRepository.findById(Long.valueOf(id))
                .orElseThrow(() -> new RuntimeException("BookCopy not found"));
        return mapToDTO(bookCopy);
    }

    public List<BookCopyDTO> getAllBookCopies() {
        List<BookCopy> bookCopies = bookCopyRepository.findAll();
        return bookCopies.stream().map(this::mapToDTO).collect(Collectors.toList());
    }

    public BookCopyDTO updateBookCopy(Integer id, BookCopyDTO bookCopyDTO) {
        BookCopy bookCopy = bookCopyRepository.findById(Long.valueOf(id))
                .orElseThrow(() -> new RuntimeException("BookCopy not found"));

        bookCopy.setInventoryNumber(bookCopyDTO.getInventoryNumber());
        bookCopy.setShelfLocation(bookCopyDTO.getShelfLocation());
        bookCopy.setNotes(bookCopyDTO.getNotes());
        // Add book mapping if necessary

        BookCopy updatedBookCopy = bookCopyRepository.save(bookCopy);
        return mapToDTO(updatedBookCopy);
    }

    public void deleteBookCopy(Integer id) {
        bookCopyRepository.deleteById(Long.valueOf(id));
    }

    private BookCopyDTO mapToDTO(BookCopy bookCopy) {
        BookCopyDTO bookCopyDTO = new BookCopyDTO();
        bookCopyDTO.setId(Math.toIntExact(bookCopy.getId()));
        bookCopyDTO.setInventoryNumber(bookCopy.getInventoryNumber());
        bookCopyDTO.setShelfLocation(bookCopy.getShelfLocation());
        bookCopyDTO.setNotes(bookCopy.getNotes());
        // Add book mapping if necessary
        return bookCopyDTO;
    }
}