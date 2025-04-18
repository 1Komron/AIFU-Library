package aifu.project.libraryweb.exsiption;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
@Getter
@Setter
public class ResponseMessage {
    private String message;
    private int status;
    private LocalDateTime timestamp;

    public ResponseMessage(String message, int status) {
        this.message = message;
        this.status = status;
        this.timestamp = LocalDateTime.now();
    }

}
