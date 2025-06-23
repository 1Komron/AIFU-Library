package aifu.project.librarybot.utils;

import org.springframework.data.domain.Page;

import java.util.HashMap;
import java.util.Map;

public class Util {
    public static Map<String, Object> getPageInfo(Page<?> page) {
        HashMap<String, Object> map = new HashMap<>();
        map.put("totalElements", page.getTotalElements());
        map.put("totalPages", page.getTotalPages());
        map.put("currentPage", page.getNumber() + 1);

        return map;
    }

    private Util() {
    }
}
