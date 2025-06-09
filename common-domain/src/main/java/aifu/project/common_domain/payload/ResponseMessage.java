package aifu.project.common_domain.payload;

public record ResponseMessage(Boolean success, String message, Object data) {
}
