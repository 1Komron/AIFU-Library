package aifu.project.common_domain.exceptions;

public class BaseBookCategoryNotFoundException extends RuntimeException {
    public BaseBookCategoryNotFoundException(Integer id) {
        super("Kategoriya topilmadi: " + id);
    }

    public BaseBookCategoryNotFoundException() {
        super("Kategoriya topilmadi");
    }
}
