package aifu.project.libraryweb.service;

import aifu.project.commondomain.entity.BaseBook;
import aifu.project.libraryweb.dto.BaseBookDTO;

import aifu.project.commondomain.repository.BaseBookRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BaseBookService {

    private final BaseBookRepository baseBookRepository;

    public BaseBookDTO createBaseBook(BaseBookDTO baseBookDTO) {
        BaseBook baseBook = mapToEntity(baseBookDTO);
        BaseBook savedBaseBook = baseBookRepository.save(baseBook);
        return mapToDTO(savedBaseBook);
    }

    public BaseBookDTO getBaseBookById(Integer id) {
        BaseBook baseBook = baseBookRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("BaseBook not found"));
        return mapToDTO(baseBook);
    }

    public List<BaseBookDTO> getAllBaseBooks() {
        List<BaseBook> baseBooks = baseBookRepository.findAll();
        return baseBooks.stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    public BaseBookDTO updateBaseBook(Integer id, BaseBookDTO baseBookDTO) {
        BaseBook baseBook = baseBookRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("BaseBook not found"));

        baseBook.setAuthor(baseBookDTO.getAuther());
        baseBook.setTitle(baseBookDTO.getTitle());
        baseBook.setSeries(baseBookDTO.getSeries());
        baseBook.setTitleDetails(baseBookDTO.getTitleDetails());
        baseBook.setPublicationYear(baseBookDTO.getPublicationYear());
        baseBook.setPublisher(baseBookDTO.getPublisher());
        baseBook.setPublicationCity(baseBookDTO.getPublicationCity());
        baseBook.setIsbn(baseBookDTO.getIsbn());
        baseBook.setPageCount(baseBookDTO.getPageCount());
        baseBook.setLanguage(baseBookDTO.getLanguage());
        baseBook.setPrice(baseBookDTO.getPrice());
        baseBook.setUdc(baseBookDTO.getUdc());

        BaseBook updated = baseBookRepository.save(baseBook);
        return mapToDTO(updated);
    }

    public void deleteBaseBook(Integer id) {
        baseBookRepository.deleteById(id);
    }

    private BaseBookDTO mapToDTO(BaseBook baseBook) {
        BaseBookDTO dto = new BaseBookDTO();
        dto.setId(baseBook.getId());
        dto.setAuther(baseBook.getAuthor());
        dto.setTitle(baseBook.getTitle());
        dto.setSeries(baseBook.getSeries());
        dto.setTitleDetails(baseBook.getTitleDetails());
        dto.setPublicationYear(baseBook.getPublicationYear());
        dto.setPublisher(baseBook.getPublisher());
        dto.setPublicationCity(baseBook.getPublicationCity());
        dto.setIsbn(baseBook.getIsbn());
        dto.setPageCount(baseBook.getPageCount());
        dto.setLanguage(baseBook.getLanguage());
        dto.setPrice(baseBook.getPrice());
        dto.setUdc(baseBook.getUdc());
        return dto;
    }

    private BaseBook mapToEntity(BaseBookDTO dto) {
        BaseBook entity = new BaseBook();
        entity.setAuthor(dto.getAuther());
        entity.setTitle(dto.getTitle());
        entity.setSeries(dto.getSeries());
        entity.setTitleDetails(dto.getTitleDetails());
        entity.setPublicationYear(dto.getPublicationYear());
        entity.setPublisher(dto.getPublisher());
        entity.setPublicationCity(dto.getPublicationCity());
        entity.setIsbn(dto.getIsbn());
        entity.setPageCount(dto.getPageCount());
        entity.setLanguage(dto.getLanguage());
        entity.setPrice(dto.getPrice());
        entity.setUdc(dto.getUdc());
        return entity;
    }
}
