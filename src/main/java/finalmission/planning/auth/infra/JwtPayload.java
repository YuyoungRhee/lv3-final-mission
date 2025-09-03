package finalmission.planning.auth.infra;

import finalmission.planning.auth.exception.UnauthorizationException;
import finalmission.planning.domain.UserRole;

public record JwtPayload(
        Long id,
        UserRole role
) {
    public JwtPayload {
        if (id == null || role == null) {
            throw new UnauthorizationException("유효하지 않은 토큰입니다.");
        }
    }
}
