package aifu.project.common_domain.dto.search_dto;

import java.util.List;

public record SearchPart(List<SearchDTO> searchDTOs,Integer currentPage,Integer totalPages) {
}
