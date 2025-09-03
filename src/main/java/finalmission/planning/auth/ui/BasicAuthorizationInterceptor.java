package finalmission.planning.auth.ui;


import static finalmission.planning.auth.constants.AuthConstants.JWT_PAYLOAD;

import finalmission.planning.auth.exception.UnauthorizationException;
import finalmission.planning.auth.infra.CookieAuthorizationExtractor;
import finalmission.planning.auth.infra.JwtPayload;
import finalmission.planning.auth.infra.JwtTokenProvider;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.Optional;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class BasicAuthorizationInterceptor implements HandlerInterceptor {

    private final CookieAuthorizationExtractor extractor;
    private final JwtTokenProvider jwtTokenProvider;

    public BasicAuthorizationInterceptor(CookieAuthorizationExtractor extractor,
                                         JwtTokenProvider jwtTokenProvider) {
        this.extractor = extractor;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {

        Optional<String> result = extractor.extract(request);
        if (result.isEmpty()) {
            throw new UnauthorizationException("로그인이 필요합니다.");
        }

        String token = result.get();
        jwtTokenProvider.validateToken(token);

        JwtPayload payLoad = jwtTokenProvider.getPayLoad(token);

        request.setAttribute(JWT_PAYLOAD, payLoad);
        return true;
    }
}
