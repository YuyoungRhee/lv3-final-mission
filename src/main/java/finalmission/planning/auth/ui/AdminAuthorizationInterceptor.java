package finalmission.planning.auth.ui;

import static finalmission.planning.auth.constants.AuthConstants.JWT_PAYLOAD;

import finalmission.planning.auth.exception.ForbiddenException;
import finalmission.planning.auth.exception.UnauthorizationException;
import finalmission.planning.auth.infra.JwtPayload;
import finalmission.planning.domain.UserRole;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class AdminAuthorizationInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        JwtPayload jwtPayload = (JwtPayload) request.getAttribute(JWT_PAYLOAD);
        if (jwtPayload == null) {
            throw new UnauthorizationException("로그인이 필요합니다.");
        }

        validateAdminRole(jwtPayload);

        request.setAttribute(JWT_PAYLOAD, jwtPayload);
        return true;
    }

    private void validateAdminRole(JwtPayload jwtPayload) {
        if (jwtPayload.role() != UserRole.ADMIN) {
            throw new ForbiddenException();
        }
    }
}
