package aifu.project.libraryweb.utils;

import org.springframework.data.domain.Page;

import java.util.Map;

public class Util {
    public static Map<String, Number> getPageInfo(Page<?> page) {
        return Map.of(
                "totalElements", page.getTotalElements(),
                "totalPages", page.getTotalPages(),
                "currentPage", page.getNumber() + 1
        );
    }

    private Util(){}
}
