package finalmission.planning.auth.infra;

import static finalmission.planning.auth.constants.AuthConstants.COOKIE_TOKEN_KEY;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.Optional;
import org.springframework.stereotype.Component;

@Component
public class CookieAuthorizationExtractor {

    public Optional<String> extract(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();

        if (cookies == null) {
            return Optional.empty();
        }

        return Arrays.stream(cookies)
                .filter(cookie -> COOKIE_TOKEN_KEY.equals(cookie.getName()))
                .findAny()
                .map(Cookie::getValue);
    }
}
