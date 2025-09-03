package finalmission.planning.auth.ui.dto;

import finalmission.planning.domain.UserRole;

public record CurrentUserInfo(
        Long id,
        String name,
        UserRole role
) {
}
