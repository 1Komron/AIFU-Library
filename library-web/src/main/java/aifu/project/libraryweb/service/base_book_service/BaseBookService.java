package aifu.project.libraryweb.service.base_book_service;

import aifu.project.common_domain.dto.live_dto.BaseBookCreateDTO;
import aifu.project.common_domain.dto.live_dto.BaseBookResponseDTO;
import aifu.project.common_domain.dto.live_dto.BaseBookUpdateDTO;

import java.util.List;

public interface BaseBookService {
    BaseBookResponseDTO create(BaseBookCreateDTO  dto);
    List<BaseBookResponseDTO> getAll();
    BaseBookResponseDTO getOne(Integer id);
    BaseBookResponseDTO update(Integer id, BaseBookUpdateDTO dto);
    void delete(Integer id);
    long countBooks();
}