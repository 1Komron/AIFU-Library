package aifu.project.libraryweb.service.BaseBook;

import aifu.project.commondomain.dto.BaseBookDTO;
import aifu.project.commondomain.entity.BaseBook;
import aifu.project.commondomain.entity.Category;
import aifu.project.commondomain.mapper.BaseBookMapper;
import aifu.project.commondomain.repository.BaseBookRepository;
import aifu.project.libraryweb.service.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BaseBookServiceImpl implements BaseBookService {

    private final BaseBookRepository baseBookRepository;
    private final CategoryService categoryService;

    @Override
    public BaseBookDTO create(BaseBookDTO dto) {
        if (baseBookRepository.existsByIsbn(dto.getIsbn())) {
            throw new RuntimeException("ISBN already exists: " + dto.getIsbn());
        }
        Category category = categoryService.getEntityById(dto.getCategoryId());
        BaseBook book = BaseBookMapper.toEntity(dto, category);
        BaseBook saved = baseBookRepository.save(book);
        return BaseBookMapper.toDto(saved);
    }

    @Override
    public BaseBookDTO update(Integer id, BaseBookDTO dto) {
        BaseBook existing = baseBookRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("BaseBook not found with id: " + id));
        if (!existing.getIsbn().equals(dto.getIsbn()) && baseBookRepository.existsByIsbn(dto.getIsbn())) {
            throw new RuntimeException("ISBN already exists: " + dto.getIsbn());
        }
        Category category = categoryService.getEntityById(dto.getCategoryId());
        existing.setAuthor(dto.getAuthor());
        existing.setTitle(dto.getTitle());
        existing.setSeries(dto.getSeries());
        existing.setTitleDetails(dto.getTitleDetails());
        existing.setPublicationYear(dto.getPublicationYear());
        existing.setPublisher(dto.getPublisher());
        existing.setPublicationCity(dto.getPublicationCity());
        existing.setIsbn(dto.getIsbn());
        existing.setPageCount(dto.getPageCount());
        existing.setLanguage(dto.getLanguage());
        existing.setPrice(dto.getPrice());
        existing.setUdc(dto.getUdc());
        existing.setCategory(category);
        BaseBook updated = baseBookRepository.save(existing);
        return BaseBookMapper.toDto(updated);
    }

    @Override
    public void delete(Integer id) {
        if (!baseBookRepository.existsById(id)) {
            throw new RuntimeException("BaseBook not found with id: " + id);
        }
        baseBookRepository.deleteById(id);
    }

    @Override
    public BaseBookDTO getById(Integer id) {
        BaseBook book = baseBookRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("BaseBook not found with id: " + id));
        return BaseBookMapper.toDto(book);
    }

    @Override
    public List<BaseBookDTO> getAll() {
        return baseBookRepository.findAll()
                .stream()
                .map(BaseBookMapper::toDto)
                .collect(Collectors.toList());
    }
}