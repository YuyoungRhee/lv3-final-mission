package finalmission.planning.auth.ui;

import static finalmission.planning.auth.constants.AuthConstants.JWT_PAYLOAD;

import finalmission.planning.auth.exception.UnauthorizationException;
import finalmission.planning.auth.infra.JwtPayload;
import finalmission.planning.auth.ui.dto.CurrentUserInfo;
import finalmission.planning.domain.User;
import finalmission.planning.infra.repository.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.core.MethodParameter;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;


@Component
@RequiredArgsConstructor
public class CurrentUserArgumentResolver implements HandlerMethodArgumentResolver {

    private final UserRepository userRepository;

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.hasParameterAnnotation(CurrentUser.class)
                && parameter.getParameterType().equals(CurrentUserInfo.class);
    }

    @Override
    public Object resolveArgument(MethodParameter parameter,
                                  ModelAndViewContainer mavContainer,
                                  NativeWebRequest webRequest,
                                  WebDataBinderFactory binderFactory) {
        HttpServletRequest request = (HttpServletRequest) webRequest.getNativeRequest();

        JwtPayload jwtPayload = (JwtPayload) request.getAttribute(JWT_PAYLOAD);
        if (jwtPayload == null) {
            throw new UnauthorizationException("로그인이 필요합니다.");
        }

        User user = userRepository.findById(jwtPayload.id())
                .orElseThrow(() -> new UnauthorizationException("유저를 찾을 수 없습니다, reservationId: " + jwtPayload.id()));
        return new CurrentUserInfo(user.getId(), user.getName(), user.getRole());
    }
}
