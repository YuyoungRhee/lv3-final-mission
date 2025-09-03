package finalmission.planning.auth.ui.dto.request;

import jakarta.validation.constraints.NotNull;

public record LoginRequest(

        @NotNull
        String email,

        @NotNull
        String password
) {
}
