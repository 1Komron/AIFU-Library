package aifu.project.libraryweb.service.BaseBook;

import aifu.project.commondomain.dto.BaseBookDTO;
import java.util.List;

public interface BaseBookService {
    BaseBookDTO create(BaseBookDTO dto);
    BaseBookDTO update(Integer id, BaseBookDTO dto);
    void delete(Integer id);
    BaseBookDTO getById(Integer id);
    List<BaseBookDTO> getAll();
}