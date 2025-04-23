package aifu.project.commondomain.payload;

public record ResponseMessage(Boolean success, String message, Object data) {
}
