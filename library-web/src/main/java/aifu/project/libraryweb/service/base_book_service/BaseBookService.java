package aifu.project.libraryweb.service.base_book_service;

import aifu.project.common_domain.dto.live_dto.BaseBookDTO;
import java.util.List;

public interface BaseBookService {
    BaseBookDTO create(BaseBookDTO dto);
    BaseBookDTO update(Integer id, BaseBookDTO dto);
    void delete(Integer id);
    BaseBookDTO getById(Integer id);
    List<BaseBookDTO> getAll();
}