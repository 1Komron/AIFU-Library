package aifu.project.common_domain.exceptions;

public class LoginBadCredentialsException extends RuntimeException {
    public LoginBadCredentialsException(String message) {
        super(message);
    }
}
