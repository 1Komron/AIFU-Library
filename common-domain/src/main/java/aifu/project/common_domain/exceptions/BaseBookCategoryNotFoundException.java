package aifu.project.common_domain.exceptions;

public class BaseBookCategoryNotFoundException extends RuntimeException {
    public BaseBookCategoryNotFoundException(Integer id) {
        super("Category not found by id: " + id);
    }
}
