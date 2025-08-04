package aifu.project.common_domain.exceptions;

public class BaseBookCategoryNotFoundException extends RuntimeException {
    public BaseBookCategoryNotFoundException(Integer id) {
        super("BaseBookCategory topilmadi: " + id);
    }
}
