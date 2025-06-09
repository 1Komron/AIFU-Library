package aifu.project.commondomain.dto;

import java.util.List;

public record SearchPart(List<SearchDTO> searchDTOs,Integer currentPage,Integer totalPages) {
}
