package aifu.project.commondomain.payload;

import jakarta.validation.constraints.NotNull;

public record RegistrationRequest(
        @NotNull String chatId,
        @NotNull Boolean accept){
}
