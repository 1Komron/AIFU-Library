package aifu.project.common_domain.exceptions;

public class BaseBookNotFoundException extends RuntimeException {
    public BaseBookNotFoundException(Integer id) {
        super("BaseBook not found by id: " + id);
    }
}
