package aifu.project.libraryweb.service.base_book_service;


import aifu.project.common_domain.dto.live_dto.BaseBookCreateDTO;
import aifu.project.common_domain.dto.live_dto.BaseBookResponseDTO;
import aifu.project.common_domain.dto.live_dto.BaseBookUpdateDTO;
import aifu.project.common_domain.entity.BaseBook;
import aifu.project.common_domain.entity.BaseBookCategory;
import aifu.project.common_domain.mapper.BaseBookMapper;
import aifu.project.libraryweb.repository.BaseBookCategoryRepository;
import aifu.project.libraryweb.repository.BaseBookRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class BaseBookServiceImpl implements BaseBookService {

    private final BaseBookRepository baseBookRepository;
    private final BaseBookCategoryRepository categoryRepository;

    public BaseBookServiceImpl(BaseBookRepository baseBookRepository, BaseBookCategoryRepository categoryRepository) {
        this.baseBookRepository = baseBookRepository;
        this.categoryRepository = categoryRepository;
    }

    @Override
    public BaseBookResponseDTO create(BaseBookCreateDTO dto) {
        BaseBookCategory category = categoryRepository.findById(dto.getCategoryId()).orElseThrow(() -> new RuntimeException("Category Not Found"));

        BaseBook entity = BaseBookMapper.toEntity(dto, category);
        baseBookRepository.save(entity);
        return BaseBookMapper.toResponseDTO(entity);
    }

    @Override
    public List<BaseBookResponseDTO> getAll() {
        return baseBookRepository.findAll().stream()
                .map(BaseBookMapper::toResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    public BaseBookResponseDTO getOne(Integer id) {
        BaseBook entity = baseBookRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("BaseBook not found"));
        return BaseBookMapper.toResponseDTO(entity);
    }

    @Override
    public BaseBookResponseDTO update(Integer id, BaseBookUpdateDTO dto) {
        BaseBook entity = baseBookRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("BaseBook not found"));

        BaseBookCategory category = categoryRepository.findById(dto.getCategoryId())
                .orElseThrow(() -> new RuntimeException("Category Not Found"));

        BaseBookMapper.updateEntity(entity, dto, category);
        baseBookRepository.save(entity);
        return BaseBookMapper.toResponseDTO(entity);
    }

    @Override
    public void delete(Integer id) {
        baseBookRepository.deleteById(id);
    }

    @Override
    public long countBooks() {
        return 0;
    }

}
