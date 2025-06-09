package aifu.project.common_domain.dto;

import java.util.concurrent.atomic.AtomicInteger;

public class PageContext {
    final String type;
    final String searchText;
    final String categoryId;
    final AtomicInteger page;

    PageContext(String type, String searchText, String categoryId, AtomicInteger page) {
        this.type = type;
        this.searchText = searchText;
        this.categoryId = categoryId;
        this.page = page;
    }
}
